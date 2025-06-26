public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // One project can have many milestones
    @OneToMany(mappedBy = "project")
    private List<Milestone> milestones;

    // One project can have many releases
    @OneToMany(mappedBy = "project")
    private List<Release> releases;
}