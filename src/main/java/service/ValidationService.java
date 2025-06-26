public class ValidationService {

    /**
     * Validates the input for milestone creation.
     * Throws ValidationException if validation fails.
     */
    public void validateMilestoneInput(MilestoneDTO dto) {
        Map<String, String[]> errors = new HashMap<>();

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.put("title", new String[]{"Title cannot be empty"});
        }
        if (dto.getStartDate() == null) {
            errors.put("start_date", new String[]{"Start date is required"});
        }
        if (dto.getDueDate() == null) {
            errors.put("due_date", new String[]{"Due date is required"});
        }
        if (dto.getStartDate() != null && dto.getDueDate() != null &&
                dto.getDueDate().isBefore(dto.getStartDate())) {
            errors.put("due_date", new String[]{"must be greater than or equal to start date"});
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }
}