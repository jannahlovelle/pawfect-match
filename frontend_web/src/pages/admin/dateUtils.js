export const formatDateForInput = (isoDate) => {
    if (!isoDate) return '';
    return new Date(isoDate).toISOString().split('T')[0];
  };
  
  export const formatDateForAPI = (dateString) => {
    if (!dateString) return null;
    return new Date(dateString).toISOString();
  };