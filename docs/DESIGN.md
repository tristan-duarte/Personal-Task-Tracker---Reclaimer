# Personal Task Tracker — Design

A simple personal task management application built as a learning project and a daily-use tool. This document captures the design decisions for v1.

## Goals

- Build a working full-stack web app end-to-end (Spring Boot REST API + Angular front-end + deployed to a public URL)
- Practice core backend skills: REST API design, JPA persistence, validation, error handling, testing
- Produce something I actually use every day to manage my own tasks

## Non-goals (v1)

- Multi-user support / authentication
- Recurring tasks
- Habit tracking (planned as a separate future project)
- Mobile app
- Real-time sync across devices

## Tech stack

| Layer | Choice | Why |
|-------|--------|-----|
| Backend framework | Spring Boot 4.0 | Familiar from coursework; industry standard for Java backends |
| Language | Java 17 | Current LTS, required by Spring Boot 4 |
| Database | MySQL 8 | Comfortable from prior work; widely used |
| ORM | Spring Data JPA (Hibernate) | Standard Spring Boot persistence layer |
| Front-end | Angular | Familiar from D387; full-featured framework |
| Build tool | Maven | Already in use; consistent with WGU coursework |
| Deployment target | TBD (likely Render or Fly.io) | Decision deferred to Week 3 |

## Domain model

### Entity: Task

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | Primary key, auto-generated | Database assigns |
| `title` | `String` | Required, max 200 chars | The short name of the task |
| `description` | `String` | Optional, max 2000 chars | Longer notes |
| `status` | `TaskStatus` | Required, default `TODO` | Enum |
| `priority` | `TaskPriority` | Required, default `MEDIUM` | Enum |
| `dueDate` | `LocalDate` | Optional | Date only, no time |
| `createdAt` | `LocalDateTime` | Auto-set on creation | Never modified after |
| `updatedAt` | `LocalDateTime` | Auto-set on creation, updated on change | Tracks last edit |

### Enums

- **`TaskStatus`**: `TODO`, `IN_PROGRESS`, `DONE`
- **`TaskPriority`**: `LOW`, `MEDIUM`, `HIGH`

## API design

All endpoints are prefixed with `/api/tasks`. Requests and responses use JSON. Timestamps are ISO 8601 in UTC.

| Method | Path | Purpose | Success | Failure |
|--------|------|---------|---------|---------|
| `GET` | `/api/tasks` | List all tasks | `200 OK` with array | — |
| `GET` | `/api/tasks/{id}` | Get one task | `200 OK` with task | `404` if not found |
| `POST` | `/api/tasks` | Create a task | `201 Created` with new task | `400` on validation error |
| `PUT` | `/api/tasks/{id}` | Update a task (full replacement) | `200 OK` with updated task | `404` if not found, `400` on validation error |
| `DELETE` | `/api/tasks/{id}` | Delete a task | `204 No Content` | `404` if not found |

### Example: Create a task

Request:
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "priority": "MEDIUM",
  "dueDate": "2026-05-20"
}
```

Response:
```http
201 Created
Content-Type: application/json

{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "status": "TODO",
  "priority": "MEDIUM",
  "dueDate": "2026-05-20",
  "createdAt": "2026-05-13T18:42:00Z",
  "updatedAt": "2026-05-13T18:42:00Z"
}
```

## Key design decisions

### Enums for status and priority, not free-text strings

Strings allow invalid values (`"Done"` vs `"DONE"` vs `"done"` vs `"DUNN"`). Enums make invalid values impossible at the type level, which is what strongly-typed systems should leverage. The API accepts and returns enum values as their string names.

### IDs in POST request bodies are ignored

A `POST /api/tasks` creates a new resource. The database assigns the ID. If a client sends an ID in the request body, the server ignores it. This is REST convention and prevents clients from attempting to overwrite arbitrary IDs.

### Hard delete, not soft delete

When a task is deleted, it's removed from the database — no "deleted" flag, no archive. Soft delete adds complexity (filtering all queries, separate restore endpoint, archive cleanup) for a feature I don't need in v1. Can be added later if I want undo functionality.

### `PUT` for updates, not `PATCH`

`PUT` is a full replacement of the resource — the client sends the entire task body. `PATCH` allows partial updates but has less consistent semantics across implementations. For v1, the simplicity of `PUT` wins. May reconsider for future versions.

### Schema managed by Hibernate (`ddl-auto=update`)

Hibernate auto-generates the schema from entity classes during development. This is the standard approach for solo Spring Boot projects. For production-grade work, this would be replaced with Flyway migrations — noted as a future enhancement.

## Open questions / future work

- **Authentication**: Not in v1. When the personal dashboard project gets built, the API will need at least an API key to prevent unauthorized writes.
- **Pagination**: `GET /api/tasks` returns all tasks. With few tasks this is fine; once I have hundreds, pagination should be added.
- **Filtering and sorting**: Planned for end of Week 1 — query parameters like `?status=DONE&sortBy=priority`.
- **Bulk operations**: Not in v1. Adding "delete all completed" or "mark several as done" can come later.
- **Database migrations**: Currently using `ddl-auto=update`. Should swap to Flyway before any production deployment.

## Out of scope (intentionally)

These were considered and explicitly excluded from v1:

- Tags or categories on tasks
- Subtasks (nested tasks)
- Reminders / notifications
- File attachments
- Search functionality
- Recurring tasks
- Multi-user / collaboration

Each could be added later. None are needed to ship a useful v1.

## References

- [Spring Boot reference documentation](https://docs.spring.io/spring-boot/)
- [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/)
- [REST API design best practices (Microsoft)](https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design)