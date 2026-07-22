# Production Scenarios: APEX Getting Started

## Scenario 1: APEX Application Unavailable After Workspace Import
**Context**: A financial services company migrated their APEX application from development to production workspace.
**Problem**: After exporting the application from DEV and importing into PROD, users received "Application not available" error. The application appeared in the application builder but could not be run.
**Root Cause**: The application was parsed as a different schema during import. The parsing schema specified during export did not match the target workspace schema. APEX could not resolve database objects because the parsing schema lacked necessary privileges.
**Solution**: 1) Identified the parsing schema mismatch by checking the application export file for the `parsing_schema` attribute. 2) Altered the application parsing schema to match the PROD schema using `APEX_APPLICATION_INSTALL`. 3) Granted necessary object privileges to the target schema. 4) Ran `APEX_APPLICATION.INSTALL` to re-sync metadata.
**Lessons Learned**: Always validate parsing schema before import. Use workspace-level grants instead of schema-level. Implement CI/CD with APEX_Session API to automate schema mapping.

## Scenario 2: Session State Contamination Across Users
**Context**: A healthcare portal built with APEX began showing patient data from one user to another.
**Problem**: Users reported seeing other patients' records after using the application simultaneously. Session state was leaking between sessions.
**Root Cause**: The application used Application-Level items with `SESSION` scope incorrectly. Custom authentication code stored shared context in `APEX_APPLICATION.G_F01` arrays without proper session isolation.
**Solution**: 1) Reviewed all application items and changed sensitive items to `REQUEST` or `PAGE` level scope. 2) Replaced global PL/SQL arrays with proper `APEX_SESSION` state management. 3) Implemented `APP_USER` checks in all data queries. 4) Tested with concurrent load simulation.
**Lessons Learned**: Never use application-level items for user-specific data. Always use `APEX_SESSION.STATE` for sensitive user context. Implement mandatory access control at the query level.

## Scenario 3: APEX Workspace URL Configuration for HTTPS
**Context**: A retail company deployed APEX on OCI and enabled SSL through the load balancer.
**Problem**: After enabling HTTPS, the application issued redirects to HTTP URLs, causing browser mixed-content warnings and broken page navigation.
**Root Cause**: APEX `WORKSPACE_BASE_URL` was configured with HTTP. The `apex_listener` configuration in ORDS had `security.https` disabled. APEX generated internal redirect URLs using the configured base URL protocol.
**Solution**: 1) Updated `WORKSPACE_BASE_URL` to use HTTPS. 2) Set `security.https: true` in ORDS `settings.xml`. 3) Configured the load balancer to forward `X-Forwarded-Proto` header. 4) Updated all static references in templates to use protocol-relative URLs.
**Lessons Learned**: Always configure APEX for HTTPS from the start. Use `APEX_UTIL.GET_HOST_URL` for dynamic URL generation. Monitor mixed-content warnings with browser dev tools after SSL migration.

## Scenario 4: Template Customization Breaking After Upgrade
**Context**: A government agency upgraded APEX from 19.2 to 23.2.
**Problem**: Customized Universal Theme templates stopped rendering correctly. Page regions appeared unstyled and navigation menus broke.
**Root Cause**: APEX 23.2 introduced Universal Theme 5.0 with significant CSS framework changes. Custom template overrides from 19.2 were incompatible with the new theme structure. The `#REGION_STATIC_ID#` substitution syntax changed.
**Solution**: 1) Reverted to default Universal Theme 5.0 templates. 2) Reapplied customizations using the new Theme Roller and CSS variables. 3) Leveraged `APEX_UTIL.GET_THEME_FILE` to verify file paths. 4) Tested all pages in a staging environment before production cutover.
**Lessons Learned**: Use Theme Roller instead of direct template edits. Maintain a theme customization inventory. Always test theme upgrades in a lower environment first. Subscribe to Oracle APES upgrade guides.
