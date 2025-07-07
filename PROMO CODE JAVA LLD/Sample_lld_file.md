# Low-Level Design (LLD) for Milestone Creation Feature

## 1. Objective- Amresh - Raghunath - Vibek

The goal of this feature is to enable project managers to create milestones within a project or group in the GitLab application. Each milestone will have a unique title (within its scope), a description, a start date, and a due date. The system will ensure that milestones are uniquely identified within their project or group, enforce business and validation rules, and persist all milestone data in a PostgreSQL database. The design will support high concurrency and meet strict performance requirements.

---

## 2. API Model

### 2.1 Common Components/Services

- **MilestoneService**: Handles business logic for milestone creation and validation.
- **MilestoneRepository**: Interface for database operations related to milestones.
- **ProjectService**: Service to fetch and validate project details.
- **GroupService**: Service to fetch and validate group details.
- **ExceptionHandler**: Handles API exceptions and validation errors.

### 2.2 API Details

| Operation                 | REST Method | Type     | URL                                 | Request JSON                                                                                   | Response JSON                                                                                 |
|---------------------------|-------------|----------|-------------------------------------|------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| Create Milestone          | POST        | Success  | /api/v1/milestones                  | {<br>  "title": "Release v1",<br>  "description": "First release milestone",<br>  "startDate": "2024-07-01",<br>  "dueDate": "2024-07-31",<br>  "projectId": 1001,<br>  "groupId": null<br>} | {<br>  "id": 1,<br>  "title": "Release v1",<br>  "description": "First release milestone",<br>  "startDate": "2024-07-01",<br>  "dueDate": "2024-07-31",<br>  "state": "active",<br>  "projectId": 1001,<br>  "groupId": null<br>} |
| Create Milestone (Failure)| POST        | Failure  | /api/v1/milestones                  | {<br>  "title": "Release v1",<br>  "description": "Duplicate milestone",<br>  "startDate": "2024-07-01",<br>  "dueDate": "2024-07-31",<br>  "projectId": 1001,<br>  "groupId": null<br>} | {<br>  "error": "Milestone title must be unique within the project or group."<br>}            |
| Create Milestone (Invalid)| POST        | Failure  | /api/v1/milestones                  | {<br>  "title": "Sprint 2",<br>  "description": "Invalid dates",<br>  "startDate": "2024-08-01",<br>  "dueDate": "2024-07-31",<br>  "projectId": 1001,<br>  "groupId": null<br>} | {<br>  "error": "Start date must be before or equal to due date."<br>}                       |

### 2.3 Exceptions

| Scenario                                            | Exception Name                  | Description                                              |
|-----------------------------------------------------|---------------------------------|----------------------------------------------------------|
| Duplicate milestone title within project/group      | DuplicateMilestoneTitleException| Thrown when a milestone with the same title exists       |
| Start date after due date                           | InvalidMilestoneDateException   | Thrown when start date is after due date                 |
| Project or group not found                          | ProjectOrGroupNotFoundException | Thrown when referenced project or group does not exist   |
| Database constraint violation                       | DataIntegrityViolationException | Thrown on DB constraint errors (e.g., unique violation)  |

---

## 3. Functional Design

### 3.1 Class Diagram

```mermaid
classDiagram
    class Milestone {
        +Long id
        +String title
        +String description
        +LocalDate startDate
        +LocalDate dueDate
        +String state
        +Long projectId
        +Long groupId
    }
    class MilestoneService {
        +createMilestone(MilestoneDTO): Milestone
    }
    class MilestoneRepository {
        +save(Milestone): Milestone
        +existsByTitleAndProjectId(String, Long): boolean
        +existsByTitleAndGroupId(String, Long): boolean
    }
    class ProjectService {
        +getProjectById(Long): Project
    }
    class GroupService {
        +getGroupById(Long): Group
    }
    class ExceptionHandler

    MilestoneService --> MilestoneRepository
    MilestoneService --> ProjectService
    MilestoneService --> GroupService
    MilestoneService --> ExceptionHandler
    MilestoneRepository --> Milestone
```

### 3.2 UML Sequence Diagram

```mermaid
sequenceDiagram
    participant User
    participant APIController
    participant MilestoneService
    participant ProjectService
    participant GroupService
    participant MilestoneRepository
    participant ExceptionHandler

    User->>APIController: POST /api/v1/milestones
    APIController->>MilestoneService: createMilestone(request)
    MilestoneService->>ProjectService: getProjectById(projectId) (if projectId present)
    MilestoneService->>GroupService: getGroupById(groupId) (if groupId present)
    MilestoneService->>MilestoneRepository: existsByTitleAndProjectId/titleAndGroupId
    alt Title exists
        MilestoneService->>ExceptionHandler: throw DuplicateMilestoneTitleException
        ExceptionHandler-->>APIController: Error Response
    else Title unique
        MilestoneService->>MilestoneRepository: save(milestone)
        MilestoneRepository-->>MilestoneService: Milestone
        MilestoneService-->>APIController: Success Response
    end
```

### 3.3 Components

| Component Name         | Purpose                                             | New/Existing |
|-----------------------|-----------------------------------------------------|--------------|
| MilestoneService      | Business logic for milestone creation/validation    | New          |
| MilestoneRepository   | DB operations for milestones                        | New          |
| ProjectService        | Fetch/validate project details                      | Existing     |
| GroupService          | Fetch/validate group details                        | Existing     |
| ExceptionHandler      | Handle and map exceptions to API responses          | Existing     |

### 3.4 Service Layer Logic and Validations

| FieldName   | Validation                                 | ErrorMessage                                         | ClassUsed           |
|-------------|--------------------------------------------|------------------------------------------------------|---------------------|
| title       | Must be unique within project or group     | Milestone title must be unique within the project or group. | MilestoneService    |
| startDate   | Must be before or equal to dueDate         | Start date must be before or equal to due date.      | MilestoneService    |
| projectId   | Must refer to an existing project (if set) | Project not found.                                   | ProjectService      |
| groupId     | Must refer to an existing group (if set)   | Group not found.                                     | GroupService        |

---

## 4. Integrations

| SystemToBeIntegrated | IntegratedFor         | IntegrationType |
|----------------------|----------------------|-----------------|
| PostgreSQL           | Milestone persistence| DB              |
| Project Service      | Project validation   | Internal API    |
| Group Service        | Group validation     | Internal API    |

---

## 5. DB Details

### 5.1 ER Model

```mermaid
erDiagram
    MILESTONE {
        BIGINT id PK
        VARCHAR title
        VARCHAR description
        DATE start_date
        DATE due_date
        VARCHAR state
        BIGINT project_id FK
        BIGINT group_id FK
    }
    PROJECT {
        BIGINT id PK
        VARCHAR name
    }
    GROUP {
        BIGINT id PK
        VARCHAR name
    }
    MILESTONE }o--|| PROJECT : "belongs to"
    MILESTONE }o--|| GROUP : "belongs to"
```

### 5.2 DB Validations

- Unique constraint: `(title, project_id)` must be unique when `project_id` is not null
- Unique constraint: `(title, group_id)` must be unique when `group_id` is not null
- `start_date` <= `due_date` (enforced in service layer)
- `project_id` or `group_id` must be present (at least one not null)

---

## 6. Non-Functional Requirements

### 6.1 Performance

- Milestone creation must complete within 2 seconds.
- Use DB-level unique constraints for fast duplicate detection.
- Use indexes on `(title, project_id)` and `(title, group_id)` for efficient lookups.
- Consider optimistic locking or transactions to handle concurrency.

### 6.2 Security
#### 6.2.1 Authentication

- All API endpoints require JWT or OAuth2 authentication.

#### 6.2.2 Authorization

- Only users with "Project Manager" or higher role can create milestones.

### 6.3 Logging

#### 6.3.1 Application Logging

- Log all milestone creation requests and responses at INFO level.
- Log validation and business errors at WARN level.

#### 6.3.2 Audit Log

- Record audit entries for milestone creation with user, timestamp, and milestone data.

---

## 7. Dependencies

- Spring Boot Framework (Web, Data JPA, Security)
- PostgreSQL database
- Project Service (internal)
- Group Service (internal)

---

## 8. Assumptions

- Either `projectId` or `groupId` must be provided in the request (not both null).
- The user is authenticated and authorized to create milestones in the specified project/group.
- Project and group entities already exist in the system.
- No soft deletes for milestones (all are persisted unless explicitly deleted).
- Date fields are provided in ISO 8601 format (YYYY-MM-DD).
- The system is horizontally scalable to handle concurrent requests.