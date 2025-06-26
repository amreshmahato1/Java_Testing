public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findById(Long id);
    Optional<Project> findByName(String name);
}