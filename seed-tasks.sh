#!/bin/bash
# seed-tasks.sh - creates sample tasks for testing

BASE="http://localhost:8080/api/tasks"

post() {
  curl -s -X POST "$BASE" -H "Content-Type: application/json" -d "$1" > /dev/null
  echo "Created: $1"
}

post '{"title":"Finish deployment setup","status":"TODO","priority":"HIGH","dueDate":"2026-05-30"}'
post '{"title":"Write service tests","status":"DONE","priority":"LOW","dueDate":"2026-05-20"}'
post '{"title":"Add filtering endpoint","status":"IN_PROGRESS","priority":"MEDIUM"}'
post '{"title":"Buy groceries","description":"Milk, eggs","status":"TODO","priority":"LOW","dueDate":"2026-05-25"}'
post '{"title":"Review PR","status":"IN_PROGRESS","priority":"HIGH"}'
post '{"title":"Read a chapter","status":"DONE","priority":"MEDIUM"}'
post '{"title":"Plan next project","status":"TODO","priority":"HIGH"}'
post '{"title":"Clean up old branches","status":"DONE","priority":"LOW"}'

echo "Done seeding tasks."