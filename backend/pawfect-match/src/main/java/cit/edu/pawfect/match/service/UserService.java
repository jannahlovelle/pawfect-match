package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.entity.UserType;
import cit.edu.pawfect.match.exception.UnauthorizedUserAccessException;
import cit.edu.pawfect.match.exception.UserNotFoundException;
import cit.edu.pawfect.match.repository.PetRepository;
import cit.edu.pawfect.match.repository.UserRepository;
import cit.edu.pawfect.match.repository.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import cit.edu.pawfect.match.dto.PetWithPhotos;
import cit.edu.pawfect.match.dto.UserProfile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PetRepository petRepository;

    public UserProfile getUserProfile(String email) {
        logger.info("Fetching user profile for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        List<PetWithPhotos> petsWithPhotos = petRepository.findByUserId(user.getUserID()).stream()
                .map(pet -> {
                    List<Photo> photos = photoRepository.findByPetId(pet.getPetId());
                    return new PetWithPhotos(pet, photos);
                })
                .collect(Collectors.toList());

        logger.info("Successfully fetched user profile for email: {}", email);
        return new UserProfile(user, petsWithPhotos);
    }

    public List<UserProfile> getAllUserProfiles() {
        logger.info("Fetching all user profiles with associated pets");
        List<User> users = userRepository.findAll();

        List<UserProfile> userProfiles = users.stream()
                .map(user -> {
                    List<PetWithPhotos> petsWithPhotos = petRepository.findByUserId(user.getUserID()).stream()
                            .map(pet -> {
                                List<Photo> photos = photoRepository.findByPetId(pet.getPetId());
                                return new PetWithPhotos(pet, photos);
                            })
                            .collect(Collectors.toList());
                    return new UserProfile(user, petsWithPhotos);
                })
                .collect(Collectors.toList());

        logger.info("Successfully fetched {} user profiles", userProfiles.size());
        return userProfiles;
    }

    public User updateUser(String userId, String authenticatedEmail, UpdateUserRequest updateRequest) {
        logger.info("Updating user with userId: {} by authenticated user with email: {}", userId, authenticatedEmail);

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found with email: {}", authenticatedEmail);
                    return new UserNotFoundException("User not found with email: " + authenticatedEmail);
                });

        if (!authenticatedUser.getUserID().equals(userId)) {
            logger.warn("Unauthorized update attempt for userId: {} by user with email: {}", userId, authenticatedEmail);
            throw new UnauthorizedUserAccessException("You are not authorized to update this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with userId: {}", userId);
                    return new UserNotFoundException("User not found with userId: " + userId);
                });

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            if (!updateRequest.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(updateRequest.getEmail())) {
                logger.error("Email already taken: {}", updateRequest.getEmail());
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            if (updateRequest.getPassword().length() < 8) {
                logger.error("Password must be at least 8 characters long for userId: {}", userId);
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            logger.info("Password updated for user with userId: {}", userId);
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        // Profile photo update is already handled here
        if (updateRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateRequest.getProfilePicture());
        } else if (updateRequest.getProfilePicture() != null && updateRequest.getProfilePicture().isEmpty()) {
            // Allow deleting the profile photo by sending an empty string
            user.setProfilePicture(null);
        }

        User updatedUser = userRepository.save(user);
        logger.info("Successfully updated user with userId: {}", userId);
        return updatedUser;
    }

    public void deleteUser(String userId, String authenticatedEmail) {
        logger.info("Deleting user with userId: {} by authenticated user with email: {}", userId, authenticatedEmail);

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found with email: {}", authenticatedEmail);
                    return new UserNotFoundException("User not found with email: " + authenticatedEmail);
                });

        if (!authenticatedUser.getUserID().equals(userId) && !authenticatedUser.getRole().equals(UserType.ADMIN)) {
            logger.warn("Unauthorized delete attempt for userId: {} by user with email: {}", userId, authenticatedEmail);
            throw new UnauthorizedUserAccessException("You are not authorized to delete this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with userId: {}", userId);
                    return new UserNotFoundException("User not found with userId: " + userId);
                });

        List<PetWithPhotos> pets = petRepository.findByUserId(userId).stream()
                .map(pet -> {
                    List<Photo> photos = photoRepository.findByPetId(pet.getPetId());
                    return new PetWithPhotos(pet, photos);
                })
                .collect(Collectors.toList());

        for (PetWithPhotos petWithPhotos : pets) {
            petWithPhotos.getPhotos().forEach(photo -> {
                logger.info("Deleting photo with ID: {} for pet with ID: {}", photo.getPhotoId(), petWithPhotos.getPet().getPetId());
                photoRepository.delete(photo);
            });
            logger.info("Deleting pet with ID: {} for user with userId: {}", petWithPhotos.getPet().getPetId(), userId);
            petRepository.delete(petWithPhotos.getPet());
        }

        userRepository.delete(user);
        logger.info("Successfully deleted user with userId: {}", userId);
    }

    public void deleteCurrentUser(String authenticatedEmail) {
        logger.info("Deleting current user with email: {}", authenticatedEmail);

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", authenticatedEmail);
                    return new UserNotFoundException("User not found with email: " + authenticatedEmail);
                });

        List<PetWithPhotos> pets = petRepository.findByUserId(user.getUserID()).stream()
                .map(pet -> {
                    List<Photo> photos = photoRepository.findByPetId(pet.getPetId());
                    return new PetWithPhotos(pet, photos);
                })
                .collect(Collectors.toList());

        for (PetWithPhotos petWithPhotos : pets) {
            petWithPhotos.getPhotos().forEach(photo -> {
                logger.info("Deleting photo with ID: {} for pet with ID: {}", photo.getPhotoId(), petWithPhotos.getPet().getPetId());
                photoRepository.delete(photo);
            });
            logger.info("Deleting pet with ID: {} for user with userId: {}", petWithPhotos.getPet().getPetId(), user.getUserID());
            petRepository.delete(petWithPhotos.getPet());
        }

        userRepository.delete(user);
        logger.info("Successfully deleted user with email: {}", authenticatedEmail);
    }

    public User findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
        logger.info("Successfully found user with email: {}", email);
        return user;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Successfully fetched {} users", users.size());
        return users;
    }

    public User adminUpdateUser(String userId, UpdateUserRequest updateRequest) {
        logger.info("Admin updating user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with userId: {}", userId);
                    return new UserNotFoundException("User not found with userId: " + userId);
                });

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            if (!updateRequest.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(updateRequest.getEmail())) {
                logger.error("Email already taken: {}", updateRequest.getEmail());
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            if (updateRequest.getPassword().length() < 8) {
                logger.error("Password must be at least 8 characters long for userId: {}", userId);
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            logger.info("Password updated for user with userId: {}", userId);
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        // Profile photo update is already handled here
        if (updateRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateRequest.getProfilePicture());
        } else if (updateRequest.getProfilePicture() != null && updateRequest.getProfilePicture().isEmpty()) {
            // Allow deleting the profile photo by sending an empty string
            user.setProfilePicture(null);
        }

        User updatedUser = userRepository.save(user);
        logger.info("Successfully updated user with userId: {}", userId);
        return updatedUser;
    }
}