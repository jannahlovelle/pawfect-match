import EditForm from './EditForm';
import { updatePet } from './api';
import { formatDateForAPI } from './dateUtils';

const PetEditForm = ({ pet, onClose, refreshPets }) => {
  const fields = [
    { label: 'Name', name: 'name', required: true },
    { label: 'Species', name: 'species', required: true },
    { label: 'Breed', name: 'breed' },
    { label: 'Gender', name: 'gender' },
    { label: 'Date of Birth', name: 'dateOfBirth', type: 'date' },
    { label: 'Weight', name: 'weight', type: 'number' },
    { label: 'Color', name: 'color' },
    { label: 'Description', name: 'description' },
    { label: 'Availability Status', name: 'availabilityStatus' },
    { label: 'Price', name: 'price', type: 'number' },
    { label: 'Pedigree Info', name: 'pedigreeInfo' },
    { label: 'Health Status', name: 'healthStatus' },
  ];

  const handleSubmit = async (formData) => {
    const payload = {
      ...formData,
      dateOfBirth: formatDateForAPI(formData.dateOfBirth),
    };
    await updatePet(pet.petId, payload);
    refreshPets();
  };

  return (
    <EditForm
      open={!!pet}
      title="Pet"
      fields={fields}
      initialData={{
        petId: pet.petId || '',
        name: pet.name || '',
        species: pet.species || '',
        breed: pet.breed || '',
        gender: pet.gender || '',
        dateOfBirth: pet.dateOfBirth || '',
        weight: pet.weight || '',
        color: pet.color || '',
        description: pet.description || '',
        availabilityStatus: pet.availabilityStatus || '',
        price: pet.price || '',
        pedigreeInfo: pet.pedigreeInfo || '',
        healthStatus: pet.healthStatus || '',
      }}
      onClose={onClose}
      onSubmit={handleSubmit}
    />
  );
};

export default PetEditForm;