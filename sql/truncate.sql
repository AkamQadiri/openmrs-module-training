-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate all tables created by the module
TRUNCATE training_course_feedback;
TRUNCATE training_exercise_attempt;
TRUNCATE training_course_enrollment;
TRUNCATE training_exercise_name;
TRUNCATE training_exercise;
TRUNCATE training_lesson_description;
TRUNCATE training_lesson_name;
TRUNCATE training_lesson;
TRUNCATE training_module_progress;
TRUNCATE training_course_module;
TRUNCATE training_course_description;
TRUNCATE training_course_name;
TRUNCATE training_course;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;