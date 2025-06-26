public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    Optional<Milestone> findByTitleAndProject(String title, Project project);
    Optional<Milestone> findByTitleAndGroup(String title, Group group);
}