-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE training_course_feedback;
TRUNCATE training_exercise_attempt;
TRUNCATE training_course_enrollment;
TRUNCATE training_module_progress;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;