# OpenMRS Training Module

An OpenMRS module for building and running training courses with lessons and practical exercises, progress tracking, and automated assessment.

## Features

- **Course Management**: Create, publish, and retire training courses
- **Multiple Exercise Types**:
  - Quiz exercises: Multiple choice, true/false, fill-in-blank, matching, ordering
  - Practical exercises: Concept creation, form creation with automated validation
- **Progress Tracking**: Monitor learner enrollment, module completion, and performance
- **Multi-language Support**: Localized content for lessons and exercises
- **Media Support**: Upload and reference images, videos, PDFs, and audio files
- **REST API**: REST API covering all training resources
- **Automated Assessment**: JSON schema-based validation for exercise submissions

## Prerequisites

- Java 1.8+
- Maven 3.x+
- OpenMRS Platform 2.7.4+
- OpenMRS Webservices.rest Module 2.32.0+

## Building from Source

```bash
mvn clean package
```

The `.omod` file will be generated in `omod/target/`.

## Installation

1. Build the module or download the `.omod` file
2. Navigate to **Administration > Manage Modules**
3. Upload and install the `.omod` file

Alternatively, copy the `.omod` to `~/.OpenMRS/modules/` and restart OpenMRS.

## Code Formatting

This module uses Spotless for code formatting:

```bash
# Apply formatting
mvn spotless:apply

# Check formatting
mvn spotless:check
```

Run `mvn spotless:apply` before committing changes.

## REST API

The module exposes REST endpoints under `/ws/rest/v1/training/`:

- **Courses**: `/course` - Manage training courses
- **Modules**: `/course/{uuid}/module` - Course module sub-resources
- **Lessons**: `/lesson` - Manage lesson content
- **Exercises**: `/exercise` - Manage exercise content
- **Attempts**: `/attempt` - Submit and track exercise attempts
- **Enrollment**: Custom controllers for enrollment management

### Example Usage

```bash
# Get all published courses
GET /ws/rest/v1/training/course?published=true

# Submit exercise attempt
POST /ws/rest/v1/training/attempt/{uuid}/submit
```

## Configuration

### Privileges

The module defines three privileges:

- **Training - Participate**: Enroll in courses and submit attempts
- **Training - Manage**: Create and manage training content
- **Training - View Analytics**: Access training reports and analytics

### Media Files

Supported formats: JPEG, PNG, GIF, WebP, SVG, MP4, WebM, PDF, MP3  
Maximum file size: 50 MB

## Future Features

- Modular export/import of course material for reuse across institutions

## License

This module is licensed under the Mozilla Public License 2.0 (MPL 2.0).

## Author

Akam Qadiri
