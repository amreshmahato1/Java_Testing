public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // One group can have many milestones
    @OneToMany(mappedBy = "group")
    private List<Milestone> milestones;
}