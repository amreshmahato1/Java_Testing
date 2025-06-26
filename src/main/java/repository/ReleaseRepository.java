public interface ReleaseRepository extends JpaRepository<Release, Long> {
    Optional<Release> findByProjectAndTagName(Project project, String tagName);
}