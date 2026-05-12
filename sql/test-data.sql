-- ===== Course 1: Mixed Content Course =====
INSERT INTO training_course (uuid, estimated_minutes, version, published, creator, date_created, retired)
VALUES (UUID(), 60, 1, TRUE, 1, NOW(), FALSE);
SET @course_id_1 = LAST_INSERT_ID();

INSERT INTO training_course_name (uuid, course_id, name, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_1, 'Mixed Content Course', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_1, 'Cours de contenu mixte', 'fr', 1, NOW(), FALSE);

INSERT INTO training_course_description (uuid, course_id, description, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_1, 'Tests course rendering with mixed content: one lesson followed by one exercise.', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_1, 'Teste le rendu de cours avec contenu mixte : une leçon suivie d''un exercice.', 'fr', 1, NOW(), FALSE);

-- ===== Course 2: Single Lesson Course =====
INSERT INTO training_course (uuid, estimated_minutes, version, published, creator, date_created, retired)
VALUES (UUID(), 30, 1, TRUE, 1, NOW(), FALSE);
SET @course_id_2 = LAST_INSERT_ID();

INSERT INTO training_course_name (uuid, course_id, name, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_2, 'Single Lesson Course', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_2, 'Cours à leçon unique', 'fr', 1, NOW(), FALSE);

INSERT INTO training_course_description (uuid, course_id, description, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_2, 'Tests single lesson course rendering and basic navigation.', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_2, 'Teste le rendu de cours à leçon unique et la navigation de base.', 'fr', 1, NOW(), FALSE);

-- ===== Course 3: Single Exercise Course =====
INSERT INTO training_course (uuid, estimated_minutes, version, published, creator, date_created, retired)
VALUES (UUID(), 15, 1, TRUE, 1, NOW(), FALSE);
SET @course_id_3 = LAST_INSERT_ID();

INSERT INTO training_course_name (uuid, course_id, name, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_3, 'Single Exercise Course', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_3, 'Cours à exercice unique', 'fr', 1, NOW(), FALSE);

INSERT INTO training_course_description (uuid, course_id, description, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_3, 'Tests single exercise course rendering and exercise submission flow.', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_3, 'Teste le rendu de cours à exercice unique et le flux de soumission d''exercice.', 'fr', 1, NOW(), FALSE);

-- ===== Course 4: Multiple Lessons Course =====
INSERT INTO training_course (uuid, estimated_minutes, version, published, creator, date_created, retired)
VALUES (UUID(), 90, 1, TRUE, 1, NOW(), FALSE);
SET @course_id_4 = LAST_INSERT_ID();

INSERT INTO training_course_name (uuid, course_id, name, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_4, 'Multiple Lessons Course', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_4, 'Cours à leçons multiples', 'fr', 1, NOW(), FALSE);

INSERT INTO training_course_description (uuid, course_id, description, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_4, 'Tests course navigation with multiple lessons. Covers all lesson content block types: heading, paragraph, list, alert, image, video, and table.', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_4, 'Teste la navigation de cours avec plusieurs leçons. Couvre tous les types de blocs de contenu : titre, paragraphe, liste, alerte, image, vidéo et tableau.', 'fr', 1, NOW(), FALSE);

-- ===== Course 5: Multiple Exercises Course =====
INSERT INTO training_course (uuid, estimated_minutes, version, published, creator, date_created, retired)
VALUES (UUID(), 75, 1, TRUE, 1, NOW(), FALSE);
SET @course_id_5 = LAST_INSERT_ID();

INSERT INTO training_course_name (uuid, course_id, name, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_5, 'Multiple Exercises Course', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_5, 'Cours à exercices multiples', 'fr', 1, NOW(), FALSE);

INSERT INTO training_course_description (uuid, course_id, description, locale, creator, date_created, voided)
VALUES 
(UUID(), @course_id_5, 'Tests exercise progression and rendering. Covers all exercise types: multiple choice, true/false, fill-in-blank, matching, ordering, concept creation, and form creation.', 'en', 1, NOW(), FALSE),
(UUID(), @course_id_5, 'Teste la progression et le rendu d''exercices. Couvre tous les types d''exercices : choix multiples, vrai/faux, texte à trous, association, ordonnancement, création de concept et création de formulaire.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 1: Introduction to OpenMRS =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"Introduction to OpenMRS","fr":"Introduction à OpenMRS"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"OpenMRS is a collaborative open-source project to develop software to support the delivery of health care in developing countries.","fr":"OpenMRS est un projet collaboratif open-source visant à développer des logiciels pour soutenir la prestation de soins de santé dans les pays en développement."}},{"type":"heading","content":{"en":"Core Concepts","fr":"Concepts fondamentaux"},"properties":{"level":2}},{"type":"list","items":[{"en":"Patient Management","fr":"Gestion des patients"},{"en":"Visit Recording","fr":"Enregistrement des visites"},{"en":"Concept Dictionary","fr":"Dictionnaire de concepts"},{"en":"Form Entry","fr":"Saisie de formulaires"}],"properties":{"ordered":false}},{"type":"alert","content":{"en":"Remember: Always save your work frequently to prevent data loss.","fr":"Rappel : Enregistrez toujours votre travail fréquemment pour éviter la perte de données."},"properties":{"variant":"info"}}]', 30, 1, NOW(), FALSE);
SET @lesson_id_1 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_1, 'Introduction to OpenMRS', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_1, 'Introduction à OpenMRS', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_1, 'Tests rendering of basic content blocks: heading (level 1), paragraph, heading (level 2), unordered list, and info alert.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_1, 'Teste le rendu des blocs de contenu de base : titre (niveau 1), paragraphe, titre (niveau 2), liste non ordonnée et alerte info.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 2: System Configuration =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"System Configuration","fr":"Configuration du système"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"Proper system configuration is essential for optimal performance.","fr":"Une configuration système appropriée est essentielle pour des performances optimales."}},{"type":"list","items":[{"en":"Configure global properties","fr":"Configurer les propriétés globales"},{"en":"Set up user roles and privileges","fr":"Configurer les rôles et privilèges utilisateur"},{"en":"Define locations","fr":"Définir les emplacements"}],"properties":{"ordered":true}},{"type":"alert","content":{"en":"Warning: Incorrect configuration may affect system behavior.","fr":"Avertissement : Une configuration incorrecte peut affecter le comportement du système."},"properties":{"variant":"warning"}}]', 20, 1, NOW(), FALSE);
SET @lesson_id_2 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_2, 'System Configuration', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_2, 'Configuration du système', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_2, 'Tests rendering of heading (level 1), paragraph, ordered list, and warning alert.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_2, 'Teste le rendu de titre (niveau 1), paragraphe, liste ordonnée et alerte d''avertissement.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 3: Working with Visual Content =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"Working with Visual Content","fr":"Travailler avec du contenu visuel"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"Visual aids help reinforce learning concepts.","fr":"Les aides visuelles aident à renforcer les concepts d''apprentissage."}},{"type":"image","properties":{"url":"https://picsum.photos/800/600","alt":{"en":"Sample medical diagram","fr":"Exemple de diagramme médical"},"caption":{"en":"Figure 1: Medical workflow diagram","fr":"Figure 1 : Diagramme de flux de travail médical"}}},{"type":"paragraph","content":{"en":"Images can illustrate complex processes clearly.","fr":"Les images peuvent illustrer clairement des processus complexes."}},{"type":"alert","content":{"en":"Success: Visual learning improves retention by up to 65%.","fr":"Succès : L''apprentissage visuel améliore la rétention jusqu''à 65 %."},"properties":{"variant":"success"}}]', 15, 1, NOW(), FALSE);
SET @lesson_id_3 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_3, 'Working with Visual Content', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_3, 'Travailler avec du contenu visuel', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_3, 'Tests rendering of heading, paragraph, URL-based image with caption, and success alert.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_3, 'Teste le rendu de titre, paragraphe, image basée sur URL avec légende et alerte de succès.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 4: Multimedia Learning Resources =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"Multimedia Learning Resources","fr":"Ressources d''apprentissage multimédia"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"Video tutorials provide step-by-step guidance.","fr":"Les tutoriels vidéo fournissent des conseils étape par étape."}},{"type":"video","properties":{"url":"https://cdn-useast1.kapwing.com/static/templates/rick-roll-video-meme-template-video-1da252ec.mp4","caption":{"en":"Tutorial: Basic Navigation","fr":"Tutoriel : Navigation de base"}}},{"type":"heading","content":{"en":"Key Takeaways","fr":"Points clés à retenir"},"properties":{"level":2}},{"type":"list","items":[{"en":"Videos demonstrate real-world scenarios","fr":"Les vidéos démontrent des scénarios réels"},{"en":"Pause and replay as needed","fr":"Mettez en pause et rejouez au besoin"},{"en":"Practice along with the tutorial","fr":"Pratiquez en suivant le tutoriel"}],"properties":{"ordered":false}}]', 25, 1, NOW(), FALSE);
SET @lesson_id_4 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_4, 'Multimedia Learning Resources', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_4, 'Ressources d''apprentissage multimédia', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_4, 'Tests rendering of heading (level 1), paragraph, URL-based video with caption, heading (level 2), and unordered list.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_4, 'Teste le rendu de titre (niveau 1), paragraphe, vidéo basée sur URL avec légende, titre (niveau 2) et liste non ordonnée.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 5: Understanding Data Structures =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"Understanding Data Structures","fr":"Comprendre les structures de données"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"Tables help organize and compare information systematically.","fr":"Les tableaux aident à organiser et comparer les informations de manière systématique."}},{"type":"table","properties":{"headers":[{"en":"Feature","fr":"Fonctionnalité"},{"en":"Community","fr":"Communauté"},{"en":"Enterprise","fr":"Entreprise"}],"rows":[[{"en":"Basic Features","fr":"Fonctionnalités de base"},{"en":"Included","fr":"Inclus"},{"en":"Included","fr":"Inclus"}],[{"en":"Advanced Reporting","fr":"Rapports avancés"},{"en":"Limited","fr":"Limité"},{"en":"Full Access","fr":"Accès complet"}],[{"en":"Support","fr":"Support"},{"en":"Community","fr":"Communauté"},{"en":"24/7 Professional","fr":"24/7 Professionnel"}]],"caption":{"en":"Table 1: Comparison of OpenMRS editions","fr":"Tableau 1 : Comparaison des éditions OpenMRS"}}},{"type":"alert","content":{"en":"Note: Choose the edition that best fits your needs.","fr":"Note : Choisissez l''édition qui correspond le mieux à vos besoins."},"properties":{"variant":"info"}}]', 20, 1, NOW(), FALSE);
SET @lesson_id_5 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_5, 'Understanding Data Structures', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_5, 'Comprendre les structures de données', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_5, 'Tests rendering of heading, paragraph, table with headers and multiple rows, and info alert.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_5, 'Teste le rendu de titre, paragraphe, tableau avec en-têtes et plusieurs lignes, et alerte info.', 'fr', 1, NOW(), FALSE);

-- ===== Lesson 6: Advanced Implementation =====
INSERT INTO training_lesson (uuid, content, estimated_minutes, creator, date_created, retired)
VALUES (UUID(), '[{"type":"heading","content":{"en":"Advanced Implementation","fr":"Mise en œuvre avancée"},"properties":{"level":1}},{"type":"paragraph","content":{"en":"This lesson covers advanced topics for experienced users.","fr":"Cette leçon couvre des sujets avancés pour les utilisateurs expérimentés."}},{"type":"heading","content":{"en":"Prerequisites","fr":"Prérequis"},"properties":{"level":2}},{"type":"list","items":[{"en":"Complete all basic courses","fr":"Terminer tous les cours de base"},{"en":"Understand core concepts","fr":"Comprendre les concepts fondamentaux"},{"en":"Have practical experience","fr":"Avoir une expérience pratique"}],"properties":{"ordered":true}},{"type":"alert","content":{"en":"Error: Do not proceed without meeting prerequisites.","fr":"Erreur : Ne pas continuer sans remplir les prérequis."},"properties":{"variant":"error"}},{"type":"paragraph","content":{"en":"Advanced features require careful planning and testing.","fr":"Les fonctionnalités avancées nécessitent une planification et des tests minutieux."}}]', 30, 1, NOW(), FALSE);
SET @lesson_id_6 = LAST_INSERT_ID();

INSERT INTO training_lesson_name (uuid, lesson_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_6, 'Advanced Implementation', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_6, 'Mise en œuvre avancée', 'fr', 1, NOW(), FALSE);

INSERT INTO training_lesson_description (uuid, lesson_id, description, locale, creator, date_created, voided)
VALUES
(UUID(), @lesson_id_6, 'Tests rendering of heading (level 1), paragraph, heading (level 2), ordered list, error alert, and additional paragraph.', 'en', 1, NOW(), FALSE),
(UUID(), @lesson_id_6, 'Teste le rendu de titre (niveau 1), paragraphe, titre (niveau 2), liste ordonnée, alerte d''erreur et paragraphe supplémentaire.', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 1: MULTIPLE_CHOICE =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'MULTIPLE_CHOICE', '{"question":{"en":"What does OpenMRS stand for?","fr":"Que signifie OpenMRS ?"},"options":[{"id":"a","text":{"en":"Open Medical Record System","fr":"Système d''enregistrement médical ouvert"}},{"id":"b","text":{"en":"Open Medical Registration Software","fr":"Logiciel d''inscription médicale ouvert"}},{"id":"c","text":{"en":"Online Medical Resource System","fr":"Système de ressources médicales en ligne"}},{"id":"d","text":{"en":"Operational Medical Records Software","fr":"Logiciel de dossiers médicaux opérationnels"}}],"hint":{"en":"Think about medical records management.","fr":"Pensez à la gestion des dossiers médicaux."}}', '{"correctAnswer":"a"}', '{"correct":{"en":"Excellent! OpenMRS stands for Open Medical Record System.","fr":"Excellent ! OpenMRS signifie Open Medical Record System."},"incorrect":{"en":"Not quite. Review the introduction lesson for the correct answer.","fr":"Pas tout à fait. Consultez la leçon d''introduction pour la bonne réponse."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_1 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_1, 'OpenMRS Acronym Quiz', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_1, 'Quiz sur l''acronyme OpenMRS', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 2: TRUE_FALSE =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'TRUE_FALSE', '{"statement":{"en":"OpenMRS is a proprietary closed-source software.","fr":"OpenMRS est un logiciel propriétaire à source fermée."}}', '{"correctAnswer":"false"}', '{"correct":{"en":"Correct! OpenMRS is open-source software.","fr":"Correct ! OpenMRS est un logiciel open-source."},"incorrect":{"en":"Incorrect. OpenMRS is actually open-source and collaborative.","fr":"Incorrect. OpenMRS est en fait open-source et collaboratif."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_2 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_2, 'OpenMRS Software Type', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_2, 'Type de logiciel OpenMRS', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 3: FILL_IN_BLANK =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'FILL_IN_BLANK', '{"text":{"en":"The {1} is the central repository for medical terminology in OpenMRS. It contains {2} that represent clinical or administrative data. Each concept can have multiple {3} in different languages.","fr":"Le {1} est le référentiel central de la terminologie médicale dans OpenMRS. Il contient des {2} qui représentent des données cliniques ou administratives. Chaque concept peut avoir plusieurs {3} dans différentes langues."},"options":[{"en":"Concept Dictionary","fr":"Dictionnaire de concepts"},{"en":"concepts","fr":"concepts"},{"en":"names","fr":"noms"},{"en":"database","fr":"base de données"},{"en":"forms","fr":"formulaires"},{"en":"patients","fr":"patients"}]}', '{"correctAnswers":{"1":"Concept Dictionary","2":"concepts","3":"names"}}', '{"correct":{"en":"Perfect! You understand the Concept Dictionary structure.","fr":"Parfait ! Vous comprenez la structure du Dictionnaire de concepts."},"incorrect":{"en":"Review the core concepts section about the Concept Dictionary.","fr":"Révisez la section sur les concepts fondamentaux du Dictionnaire de concepts."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_3 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_3, 'Concept Dictionary Knowledge', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_3, 'Connaissance du dictionnaire de concepts', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 4: MATCHING =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'MATCHING', '{"instructions":{"en":"Match each OpenMRS component with its primary function.","fr":"Associez chaque composant OpenMRS à sa fonction principale."},"leftItems":[{"id":"1","text":{"en":"Patient Management"}},{"id":"2","text":{"en":"Concept Dictionary"}},{"id":"3","text":{"en":"Form Entry"}},{"id":"4","text":{"en":"Visit Management"}}],"rightItems":[{"id":"a","text":{"en":"Record clinical observations","fr":"Enregistrer les observations cliniques"}},{"id":"b","text":{"en":"Track patient encounters","fr":"Suivre les rencontres des patients"}},{"id":"c","text":{"en":"Manage patient demographics","fr":"Gérer les données démographiques des patients"}},{"id":"d","text":{"en":"Define medical terminology","fr":"Définir la terminologie médicale"}}]}', '{"correctMatches":{"1":"c","2":"d","3":"a","4":"b"}}', '{"correct":{"en":"Excellent! You understand the core components of OpenMRS.","fr":"Excellent ! Vous comprenez les composants principaux d''OpenMRS."},"incorrect":{"en":"Review the core concepts section to understand each component better.","fr":"Révisez la section sur les concepts fondamentaux pour mieux comprendre chaque composant."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_4 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_4, 'Component Matching', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_4, 'Association de composants', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 5: ORDERING =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'ORDERING', '{"instructions":{"en":"Arrange the following steps in the correct order for setting up a new OpenMRS installation:","fr":"Classez les étapes suivantes dans le bon ordre pour configurer une nouvelle installation OpenMRS :"},"items":[{"id":"1","text":{"en":"Install required dependencies","fr":"Installer les dépendances requises"}},{"id":"2","text":{"en":"Download OpenMRS","fr":"Télécharger OpenMRS"}},{"id":"3","text":{"en":"Configure database connection","fr":"Configurer la connexion à la base de données"}},{"id":"4","text":{"en":"Run initial setup wizard","fr":"Exécuter l''assistant de configuration initiale"}},{"id":"5","text":{"en":"Create admin user","fr":"Créer un utilisateur administrateur"}},{"id":"6","text":{"en":"Configure global properties","fr":"Configurer les propriétés globales"}}]}', '{"correctOrder":["1","2","3","4","5","6"]}', '{"correct":{"en":"Perfect! You know the correct installation sequence.","fr":"Parfait ! Vous connaissez la séquence d''installation correcte."},"incorrect":{"en":"Review the system configuration lesson for the proper installation order.","fr":"Révisez la leçon de configuration du système pour l''ordre d''installation approprié."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_5 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_5, 'Installation Steps Order', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_5, 'Ordre des étapes d''installation', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 6: CONCEPT_CREATION =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'CONCEPT_CREATION', '{"instructions":{"en":"Create a concept for Blood Pressure measurement according to the specifications below.","fr":"Créez un concept pour la mesure de la pression artérielle selon les spécifications ci-dessous."},"requirements":{"name":{"primary":{"en":"Blood Pressure","fr":"Pression artérielle"},"synonyms":[{"en":"BP","fr":"TA"},{"en":"Arterial Pressure","fr":"Pression artérielle"}],"shortName":{"en":"BP","fr":"TA"}},"description":{"en":"Measurement of blood pressure including systolic and diastolic values","fr":"Mesure de la pression artérielle incluant les valeurs systolique et diastolique"},"class":"Finding","datatype":"N/A","isSet":true,"setMembers":[{"conceptName":{"en":"Systolic Blood Pressure","fr":"Pression artérielle systolique"}},{"conceptName":{"en":"Diastolic Blood Pressure","fr":"Pression artérielle diastolique"}}]}}', '{}', '{"correct":{"en":"Excellent work! Your Blood Pressure concept is correctly configured.","fr":"Excellent travail ! Votre concept de pression artérielle est correctement configuré."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_6 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_6, 'Blood Pressure Concept Creation', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_6, 'Création du concept de pression artérielle', 'fr', 1, NOW(), FALSE);

-- ===== Exercise 7: FORM_CREATION =====
INSERT INTO training_exercise (uuid, exercise_type, content, validation, feedback, allow_retry, creator, date_created, retired)
VALUES (UUID(), 'FORM_CREATION', '{"instructions":{"en":"Create a Vital Signs form according to the specifications below. The form should capture basic vital sign measurements.","fr":"Créez un formulaire de signes vitaux selon les spécifications ci-dessous. Le formulaire doit capturer les mesures de base des signes vitaux."},"requirements":{"name":{"en":"Vital Signs Form","fr":"Formulaire de signes vitaux"},"description":{"en":"A comprehensive form for recording vital signs measurements","fr":"Un formulaire complet pour enregistrer les mesures des signes vitaux"},"version":"1.0","encounterType":"Vitals","published":false,"pages":[{"label":{"en":"Vital Signs","fr":"Signes vitaux"},"sections":[{"label":{"en":"Basic Measurements","fr":"Mesures de base"},"isExpanded":true,"questions":[{"id":{"en":"weight","fr":"poids"},"label":{"en":"Weight (kg)","fr":"Poids (kg)"},"required":true,"type":"obs","questionOptions":{"rendering":"number","concept":{"id":"5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":{"en":"Weight (kg)","fr":"Poids (kg)"}},"min":"0","max":"500"}},{"id":{"en":"height","fr":"taille"},"label":{"en":"Height (cm)","fr":"Taille (cm)"},"required":true,"type":"obs","questionOptions":{"rendering":"number","concept":{"id":"5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":{"en":"Height (cm)","fr":"Taille (cm)"}},"min":"0","max":"300"}},{"id":{"en":"temperature","fr":"température"},"label":{"en":"Temperature (°C)","fr":"Température (°C)"},"required":true,"type":"obs","questionOptions":{"rendering":"number","concept":{"id":"5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":{"en":"Temperature (C)","fr":"Température (C)"}},"min":"30","max":"45"}}]}]}]}}', '{}', '{"correct":{"en":"Perfect! Your Vital Signs form is correctly structured with all required fields.","fr":"Parfait ! Votre formulaire de signes vitaux est correctement structuré avec tous les champs requis."}}', TRUE, 1, NOW(), FALSE);
SET @exercise_id_7 = LAST_INSERT_ID();

INSERT INTO training_exercise_name (uuid, exercise_id, name, locale, creator, date_created, voided)
VALUES
(UUID(), @exercise_id_7, 'Vital Signs Form Creation', 'en', 1, NOW(), FALSE),
(UUID(), @exercise_id_7, 'Création du formulaire de signes vitaux', 'fr', 1, NOW(), FALSE);

-- ===== Course Modules =====
-- Course 1: Mixed Content Course (1 lesson + 1 exercise)
INSERT INTO training_course_module (uuid, course_id, lesson_id, exercise_id, module_type, sort_weight, required, creator, date_created, voided)
VALUES
(UUID(), @course_id_1, @lesson_id_1, NULL, 'LESSON', 1, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_1, NULL, @exercise_id_1, 'EXERCISE', 2, TRUE, 1, NOW(), FALSE);

-- Course 2: Single Lesson Course
INSERT INTO training_course_module (uuid, course_id, lesson_id, exercise_id, module_type, sort_weight, required, creator, date_created, voided)
VALUES
(UUID(), @course_id_2, @lesson_id_1, NULL, 'LESSON', 1, TRUE, 1, NOW(), FALSE);

-- Course 3: Single Exercise Course
INSERT INTO training_course_module (uuid, course_id, lesson_id, exercise_id, module_type, sort_weight, required, creator, date_created, voided)
VALUES
(UUID(), @course_id_3, NULL, @exercise_id_2, 'EXERCISE', 1, TRUE, 1, NOW(), FALSE);

-- Course 4: Multiple Lessons Course (all content types)
INSERT INTO training_course_module (uuid, course_id, lesson_id, exercise_id, module_type, sort_weight, required, creator, date_created, voided)
VALUES
(UUID(), @course_id_4, @lesson_id_1, NULL, 'LESSON', 1, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_4, @lesson_id_2, NULL, 'LESSON', 2, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_4, @lesson_id_3, NULL, 'LESSON', 3, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_4, @lesson_id_4, NULL, 'LESSON', 4, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_4, @lesson_id_5, NULL, 'LESSON', 5, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_4, @lesson_id_6, NULL, 'LESSON', 6, TRUE, 1, NOW(), FALSE);

-- Course 5: Multiple Exercises Course (all exercise types)
INSERT INTO training_course_module (uuid, course_id, lesson_id, exercise_id, module_type, sort_weight, required, creator, date_created, voided)
VALUES
(UUID(), @course_id_5, NULL, @exercise_id_1, 'EXERCISE', 1, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_2, 'EXERCISE', 2, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_3, 'EXERCISE', 3, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_4, 'EXERCISE', 4, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_5, 'EXERCISE', 5, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_6, 'EXERCISE', 6, TRUE, 1, NOW(), FALSE),
(UUID(), @course_id_5, NULL, @exercise_id_7, 'EXERCISE', 7, TRUE, 1, NOW(), FALSE);