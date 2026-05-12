-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables created by the module
DROP TABLE IF EXISTS training_course_feedback;
DROP TABLE IF EXISTS training_exercise_attempt;
DROP TABLE IF EXISTS training_course_enrollment;
DROP TABLE IF EXISTS training_exercise_name;
DROP TABLE IF EXISTS training_exercise;
DROP TABLE IF EXISTS training_lesson_description;
DROP TABLE IF EXISTS training_lesson_name;
DROP TABLE IF EXISTS training_lesson;
DROP TABLE IF EXISTS training_module_progress;
DROP TABLE IF EXISTS training_course_module;
DROP TABLE IF EXISTS training_course_description;
DROP TABLE IF EXISTS training_course_name;
DROP TABLE IF EXISTS training_course;

-- Remove roles created by the module
DELETE FROM role_privilege WHERE role IN ('Training: Student', 'Training: Instructor', 'Training: Analyst');
DELETE FROM user_role WHERE role IN ('Training: Student', 'Training: Instructor', 'Training: Analyst');
DELETE FROM role WHERE role IN ('Training: Student', 'Training: Instructor', 'Training: Analyst');
DELETE FROM role_role WHERE child_role IN ('Training: Student', 'Training: Instructor', 'Training: Analyst');

-- Remove privileges created by the module
DELETE FROM role_privilege WHERE privilege IN (
    'Training - Participate',
    'Training - Manage', 
    'Training - View Analytics',
    'Edit Forms'
);
DELETE FROM privilege WHERE privilege IN (
    'Training - Participate',
    'Training - Manage', 
    'Training - View Analytics',
    'Edit Forms'
);

-- Remove the training media concept (if it exists)
-- First remove any observations using this concept
DELETE FROM obs WHERE concept_id = (
    SELECT concept_id FROM concept WHERE uuid = 'a8a0f3a2-1350-11df-a1f1-0026b9348838'
);

-- Remove concept name
DELETE FROM concept_name WHERE concept_id = (
    SELECT concept_id FROM concept WHERE uuid = 'a8a0f3a2-1350-11df-a1f1-0026b9348838'
);

-- Remove concept description
DELETE FROM concept_description WHERE concept_id = (
    SELECT concept_id FROM concept WHERE uuid = 'a8a0f3a2-1350-11df-a1f1-0026b9348838'
);

-- Remove the concept itself
DELETE FROM concept_complex WHERE concept_id = (
    SELECT concept_id FROM concept WHERE uuid = 'a8a0f3a2-1350-11df-a1f1-0026b9348838'
);
DELETE FROM concept WHERE uuid = 'a8a0f3a2-1350-11df-a1f1-0026b9348838';

-- Remove liquibase changelog entries to allow clean reinstall
DELETE FROM liquibasechangelog WHERE id LIKE 'training-%';

-- Remove any module-specific global properties (if you add any in the future)
DELETE FROM global_property WHERE property LIKE 'training.%';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;