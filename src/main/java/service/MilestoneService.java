public class MilestoneService {

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GroupService groupService;

    /**
     * Creates a milestone for a project.
     * @param projectId Project ID
     * @param dto MilestoneDTO
     * @return Created Milestone
     */
    @Transactional
    public Milestone createMilestoneForProject(Long projectId, MilestoneDTO dto) {
        validationService.validateMilestoneInput(dto);
        Project project = projectService.getProject(projectId);

        // Check uniqueness of title within project
        if (milestoneRepository.findByTitleAndProject(dto.getTitle(), project).isPresent()) {
            Map<String, String[]> errors = new HashMap<>();
            errors.put("title", new String[]{"has already been taken"});
            throw new ValidationException("Validation failed", errors);
        }

        Milestone milestone = new Milestone();
        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setStartDate(dto.getStartDate());
        milestone.setDueDate(dto.getDueDate());
        milestone.setState("active");
        milestone.setCreatedAt(LocalDateTime.now());
        milestone.setUpdatedAt(LocalDateTime.now());
        milestone.setProject(project);
        milestone.setGroup(null); // Not a group milestone

        return milestoneRepository.save(milestone);
    }

    /**
     * Creates a milestone for a group.
     * @param groupId Group ID
     * @param dto MilestoneDTO
     * @return Created Milestone
     */
    @Transactional
    public Milestone createMilestoneForGroup(Long groupId, MilestoneDTO dto) {
        validationService.validateMilestoneInput(dto);
        Group group = groupService.getGroup(groupId);

        // Check uniqueness of title within group
        if (milestoneRepository.findByTitleAndGroup(dto.getTitle(), group).isPresent()) {
            Map<String, String[]> errors = new HashMap<>();
            errors.put("title", new String[]{"has already been taken"});
            throw new ValidationException("Validation failed", errors);
        }

        Milestone milestone = new Milestone();
        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setStartDate(dto.getStartDate());
        milestone.setDueDate(dto.getDueDate());
        milestone.setState("active");
        milestone.setCreatedAt(LocalDateTime.now());
        milestone.setUpdatedAt(LocalDateTime.now());
        milestone.setGroup(group);
        milestone.setProject(null); // Not a project milestone

        return milestoneRepository.save(milestone);
    }

    /**
     * Retrieves a milestone by its ID.
     * @param milestoneId Milestone ID
     * @return Milestone entity
     */
    public Milestone getMilestone(Long milestoneId) {
        return milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));
    }

    /**
     * Updates milestone progress (for association with release).
     * @param milestone Milestone entity
     */
    @Transactional
    public void updateProgress(Milestone milestone) {
        // For demonstration, just set progress to 100 when associated with a release
        milestone.setProgress(100);
        milestone.setUpdatedAt(LocalDateTime.now());
        milestoneRepository.save(milestone);
    }
}