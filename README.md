# Tasks Implementation Summary

This solution is implemented in Java using Spring Boot, Spring MVC, Spring Data JPA, Thymeleaf and an H2 database.

To avoid re-entering all sectors manually, I created a small helper script (SectorExtractor) that parses the original index.html, reads all <option> elements (including their hierarchy), and generates an SQL script with INSERT statements. This script is used as data.sql so that all sectors are loaded into the H2 database automatically on application startup.

The domain model consists of two entities: Sector and Form. Sector represents the hierarchical sector list, while Form stores the submitted data (name, selected sectors, and agreement to terms) in a form table and a form_sector join table.

The original index.html was rewritten as a Thymeleaf template (newIndex.html) to fix the deficiencies and to bind directly to a FormDto. All fields are validated on the server side (all mandatory), and after a successful save the form is repopulated from the database and kept editable within the same HTTP session. The UI is intentionally minimal, as no specific design requirements were given.

Finally, I added a few tests (service and controller level) to cover the main functionalities: sector hierarchy building, form validation, persistence, and editing the same form within a session.

Full database dump (structure and data) is provided as database_dump.sql.
