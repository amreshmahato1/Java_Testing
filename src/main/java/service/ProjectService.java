public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Retrieves a project by its ID.
     * @param id Project ID
     * @return Project entity
     * @throws ResourceNotFoundException if not found
     */
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}