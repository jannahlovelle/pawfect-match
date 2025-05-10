package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.CreatePetRequest;
import cit.edu.pawfect.match.dto.PetFeedResponse;
import cit.edu.pawfect.match.dto.UpdatePetRequest;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.repository.PetRepository;
import cit.edu.pawfect.match.repository.PhotoRepository;
import cit.edu.pawfect.match.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes"})
@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unnamed_" + UUID.randomUUID().toString().substring(0, 8);
        String name = fileName.replaceFirst("[.][^.]+$", "")
                .replaceAll("[^a-zA-Z0-9_-]", "_")
                .replaceAll("_+", "_")
                .trim();
        return name.isEmpty() ? "unnamed_" + UUID.randomUUID().toString().substring(0, 8) : name;
    }

    private String getUniquePublicId(String basePublicId, String petId) throws IOException {
        String publicId = basePublicId;
        int suffix = 0;
        int maxRetries = 3;
        while (true) {
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    logger.info("Checking Cloudinary public_id: {}, attempt: {}", publicId, attempt);
                    cloudinary.api().resource(publicId, ObjectUtils.asMap("resource_type", "image"));
                    logger.info("Found existing public_id: {}", publicId);
                    suffix++;
                    publicId = basePublicId + "_" + suffix;
                    break;
                } catch (Exception e) {
                    if (e.getMessage().contains("not found")) {
                        logger.info("Public_id available: {}", publicId);
                        return publicId;
                    }
                    logger.error("Error checking Cloudinary public_id: {}, attempt: {}, error: {}", publicId, attempt, e.getMessage());
                    if (attempt == maxRetries) {
                        throw new IOException("Failed to verify public_id after " + maxRetries + " attempts: " + e.getMessage());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during public_id check: " + ie.getMessage());
                    }
                }
            }
        }
    }

    public Pet createPet(String userId, CreatePetRequest petRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(petRequest.getName());
        pet.setSpecies(petRequest.getSpecies());
        pet.setBreed(petRequest.getBreed());
        pet.setGender(petRequest.getGender());
        pet.setDateOfBirth(petRequest.getDateOfBirth());
        pet.setWeight(petRequest.getWeight());
        pet.setColor(petRequest.getColor());
        pet.setDescription(petRequest.getDescription());
        pet.setAvailabilityStatus(petRequest.getAvailabilityStatus());
        pet.setPrice(petRequest.getPrice());
        pet.setPedigreeInfo(petRequest.getPedigreeInfo());
        pet.setHealthStatus(petRequest.getHealthStatus());

        Pet savedPet = petRepository.save(pet);
        logger.info("Created pet with ID: {} for userId: {}", savedPet.getPetId(), userId);
        return savedPet;
    }
    public Pet getPetById(String petId) {
    return petRepository.findById(petId)
            .orElseThrow(() -> {
                logger.error("Pet not found with ID: {}", petId);
                return new RuntimeException("Pet not found with ID: " + petId);
            });
}


public Pet getPetByIdAndUserId(String petId, String userId) {
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> {
                logger.error("Pet not found with ID: {}", petId);
                return new RuntimeException("Pet not found with ID: " + petId);
            });
    
    if (!pet.getUserId().equals(userId)) {
        logger.warn("Unauthorized access for petId: {} by userId: {}", petId, userId);
        throw new RuntimeException("You are not authorized to access this pet");
    }
    
    return pet;
}

    public Photo addPhoto(String petId, String authenticatedEmail, MultipartFile file) throws IOException {
        logger.info("Adding photo for petId: {} by email: {}", petId, authenticatedEmail);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found with ID: " + petId);
                });

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", authenticatedEmail);
                    return new RuntimeException("User not found with email: " + authenticatedEmail);
                });

        if (!pet.getUserId().equals(user.getUserID())) {
            logger.warn("Unauthorized photo upload for petId: {} by email: {}", petId, authenticatedEmail);
            throw new RuntimeException("You are not authorized to add photos for this pet");
        }

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String publicId = getUniquePublicId("pawfectmatch/pets/" + fileName, petId);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pawfectmatch/pets",
                        "public_id", publicId,
                        "transformation", new com.cloudinary.Transformation()
                                .width(800).height(800).crop("limit")
                ));

        Photo photo = new Photo();
        photo.setPhotoId(UUID.randomUUID().toString());
        photo.setPetId(petId);
        photo.setUrl((String) uploadResult.get("secure_url"));
        photo.setCloudinaryPublicId((String) uploadResult.get("public_id"));

        Photo savedPhoto = photoRepository.save(photo);
        logger.info("Added photo with ID: {} for petId: {}, public_id: {}", savedPhoto.getPhotoId(), petId, publicId);
        return savedPhoto;
    }

    public List<Pet> getPetsByUserId(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        List<Pet> pets = petRepository.findByUserId(userId);
        logger.info("Retrieved {} pets for userId: {}", pets.size(), userId);
        return pets;
    }

    public List<Photo> getPhotosByPetId(String petId) {
        petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found with ID: " + petId);
                });

        List<Photo> photos = photoRepository.findByPetId(petId);
        logger.info("Retrieved {} photos for petId: {}", photos.size(), petId);
        return photos;
    }

   public Pet updatePet(String petId, String userId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found with ID: " + petId);
                });

        if (!pet.getUserId().equals(userId)) {
            logger.warn("Unauthorized update for petId: {} by userId: {}", petId, userId);
            throw new RuntimeException("You are not authorized to update this pet");
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getGender() != null) {
            pet.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            pet.setDateOfBirth(java.sql.Timestamp.valueOf(request.getDateOfBirth()));
        }
        if (request.getWeight() != null) {
            pet.setWeight(request.getWeight());
        }
        if (request.getColor() != null) {
            pet.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            pet.setDescription(request.getDescription());
        }
        if (request.getAvailabilityStatus() != null) {
            pet.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        if (request.getPrice() != null) {
            pet.setPrice(request.getPrice());
        }
        if (request.getPedigreeInfo() != null) {
            pet.setPedigreeInfo(request.getPedigreeInfo());
        }
        if (request.getHealthStatus() != null) {
            pet.setHealthStatus(request.getHealthStatus());
        }

        Pet updatedPet = petRepository.save(pet);
        logger.info("Updated pet with ID: {}", petId);
        return updatedPet;
    }


    public void deletePet(String petId, String userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found with ID: " + petId);
                });

        if (!pet.getUserId().equals(userId)) {
            logger.warn("Unauthorized delete for petId: {} by userId: {}", petId, userId);
            throw new RuntimeException("You are not authorized to delete this pet");
        }

        List<Photo> photos = photoRepository.findByPetId(petId);
        for (Photo photo : photos) {
            deletePhotoFromCloudinary(photo);
        }
        photoRepository.deleteByPetId(petId);
        petRepository.deleteById(petId);
        logger.info("Deleted pet with ID: {}", petId);
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        logger.info("Retrieved {} pets", pets.size());
        return pets;
    }

    public Pet adminUpdatePet(String petId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found with ID: " + petId);
                });

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getGender() != null) {
            pet.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            pet.setDateOfBirth(java.sql.Timestamp.valueOf(request.getDateOfBirth()));
        }
        if (request.getWeight() != null) {
            pet.setWeight(request.getWeight());
        }
        if (request.getColor() != null) {
            pet.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            pet.setDescription(request.getDescription());
        }
        if (request.getAvailabilityStatus() != null) {
            pet.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        if (request.getPrice() != null) {
            pet.setPrice(request.getPrice());
        }
        if (request.getPedigreeInfo() != null) {
            pet.setPedigreeInfo(request.getPedigreeInfo());
        }
        if (request.getHealthStatus() != null) {
            pet.setHealthStatus(request.getHealthStatus());
        }

        Pet updatedPet = petRepository.save(pet);
        logger.info("Admin updated pet with ID: {}", petId);
        return updatedPet;
    }

    public void adminDeletePet(String petId) {
        if (!petRepository.existsById(petId)) {
            logger.error("Pet not found with ID: {}", petId);
            throw new RuntimeException("Pet not found with ID: " + petId);
        }

        List<Photo> photos = photoRepository.findByPetId(petId);
        for (Photo photo : photos) {
            deletePhotoFromCloudinary(photo);
        }
        photoRepository.deleteByPetId(petId);
        petRepository.deleteById(petId);
        logger.info("Admin deleted pet with ID: {}", petId);
    }

    public Photo updatePetPhoto(String photoId, String authenticatedEmail, MultipartFile file) throws IOException {
        logger.info("Updating photo with ID: {} by email: {}", photoId, authenticatedEmail);
    
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> {
                    logger.error("Photo not found with ID: {}", photoId);
                    return new RuntimeException("Photo not found with ID: " + photoId);
                });
    
        Pet pet = petRepository.findById(photo.getPetId())
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", photo.getPetId());
                    return new RuntimeException("Pet not found with ID: " + photo.getPetId());
                });
    
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", authenticatedEmail);
                    return new RuntimeException("User not found with email: " + authenticatedEmail);
                });
    
        if (!pet.getUserId().equals(user.getUserID())) {
            logger.warn("Unauthorized photo update for photoId: {} by email: {}", photoId, authenticatedEmail);
            throw new RuntimeException("You are not authorized to update this pet's photo");
        }
    
        deletePhotoFromCloudinary(photo);
    
        String fileName = sanitizeFileName(file.getOriginalFilename());
        String basePublicId = "pawfectmatch/pets/" + fileName;
    
        String publicId = getUniquePublicId(basePublicId, pet.getPetId());
    
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pawfectmatch/pets",
                        "public_id", publicId,
                        "transformation", new com.cloudinary.Transformation()
                                .width(800).height(800).crop("limit")
                ));
    
        photo.setUrl((String) uploadResult.get("secure_url"));
        photo.setCloudinaryPublicId((String) uploadResult.get("public_id"));
    
        Photo updatedPhoto = photoRepository.save(photo);
        logger.info("Updated photo with ID: {} for petId: {}, public_id: {}", photoId, photo.getPetId(), publicId);
        return updatedPhoto;
    }
    
    public void deletePetPhoto(String photoId, String authenticatedEmail) {
        logger.info("Deleting photo with ID: {} by email: {}", photoId, authenticatedEmail);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> {
                    logger.error("Photo not found with ID: {}", photoId);
                    return new RuntimeException("Photo not found with ID: " + photoId);
                });

        Pet pet = petRepository.findById(photo.getPetId())
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", photo.getPetId());
                    return new RuntimeException("Pet not found with ID: " + photo.getPetId());
                });

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", authenticatedEmail);
                    return new RuntimeException("User not found with email: " + authenticatedEmail);
                });

        if (!pet.getUserId().equals(user.getUserID())) {
            logger.warn("Unauthorized photo delete for photoId: {} by email: {}", photoId, authenticatedEmail);
            throw new RuntimeException("You are not authorized to delete this pet's photo");
        }

        deletePhotoFromCloudinary(photo);
        photoRepository.delete(photo);
        logger.info("Deleted photo with ID: {}", photoId);
    }

    public Photo adminUpdatePetPhoto(String photoId, MultipartFile file) throws IOException {
        logger.info("Admin updating photo with ID: {}", photoId);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> {
                    logger.error("Photo not found with ID: {}", photoId);
                    return new RuntimeException("Photo not found with ID: " + photoId);
                });

        deletePhotoFromCloudinary(photo);

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String publicId = getUniquePublicId("pawfectmatch/pets/" + fileName, photo.getPetId());

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pawfectmatch/pets",
                        "public_id", publicId,
                        "transformation", new com.cloudinary.Transformation()
                                .width(800).height(800).crop("limit")
                ));

        photo.setUrl((String) uploadResult.get("secure_url"));
        photo.setCloudinaryPublicId((String) uploadResult.get("public_id"));

        Photo updatedPhoto = photoRepository.save(photo);
        logger.info("Admin updated photo with ID: {} for petId: {}, public_id: {}", photoId, photo.getPetId(), publicId);
        return updatedPhoto;
    }

    public void adminDeletePetPhoto(String photoId) {
        logger.info("Admin deleting photo with ID: {}", photoId);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> {
                    logger.error("Photo not found with ID: {}", photoId);
                    return new RuntimeException("Photo not found with ID: " + photoId);
                });

        deletePhotoFromCloudinary(photo);
        photoRepository.delete(photo);
        logger.info("Admin deleted photo with ID: {}", photoId);
    }

    private void deletePhotoFromCloudinary(Photo photo) {
        if (photo.getCloudinaryPublicId() != null) {
            try {
                cloudinary.uploader().destroy(photo.getCloudinaryPublicId(), ObjectUtils.emptyMap());
                logger.info("Deleted Cloudinary photo with public_id: {}", photo.getCloudinaryPublicId());
            } catch (IOException e) {
                logger.error("Failed to delete Cloudinary photo with public_id: {}: {}",
                             photo.getCloudinaryPublicId(), e.getMessage());
            }
        } else {
            logger.info("No Cloudinary public_id for photoId: {}, skipping Cloudinary deletion", photo.getPhotoId());
        }
    }

    public List<PetFeedResponse> getPetsForFeed(String species, int page, int size) {
        List<Pet> pets;
        if (species != null && !species.trim().isEmpty()) {
            pets = petRepository.findBySpeciesAndAvailabilityStatus(species, "available");
        } else {
            pets = petRepository.findByAvailabilityStatus("available");
        }

        List<Pet> petsWithPhotos = pets.stream()
                .filter(pet -> !photoRepository.findByPetId(pet.getPetId()).isEmpty())
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, petsWithPhotos.size());
        if (start >= petsWithPhotos.size()) {
            return new ArrayList<>();
        }
        petsWithPhotos = petsWithPhotos.subList(start, end);

        List<PetFeedResponse> feed = petsWithPhotos.stream().map(pet -> {
            List<Photo> photos = photoRepository.findByPetId(pet.getPetId());
            String photoUrl = photos.get(0).getUrl();
            return new PetFeedResponse(
                pet.getPetId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                photoUrl,
                pet.getDescription()
            );
        }).collect(Collectors.toList());

        logger.info("Retrieved {} pets for feed, species: {}, page: {}, size: {}", feed.size(), species != null ? species : "all", page, size);
        return feed;
    }
}