public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findById(Long id);
    Optional<Group> findByName(String name);
}