public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    /**
     * Retrieves a group by its ID.
     * @param id Group ID
     * @return Group entity
     * @throws ResourceNotFoundException if not found
     */
    public Group getGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }
}