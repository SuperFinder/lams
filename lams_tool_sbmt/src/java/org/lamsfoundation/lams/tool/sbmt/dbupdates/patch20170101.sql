-- Turn off autocommit, so nothing is committed if there is an error
SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS=0;
----------------------Put all sql statements below here-------------------------

-- LDEV-4180
ALTER TABLE tl_lasbmt11_content MODIFY COLUMN lock_on_finished TINYINT(1),
								MODIFY COLUMN content_in_use TINYINT(1),
								MODIFY COLUMN define_later TINYINT(1),
								MODIFY COLUMN reflect_on_activity TINYINT(1),
								MODIFY COLUMN limit_upload TINYINT(1),
								MODIFY COLUMN mark_release_notify TINYINT(1) DEFAULT 0,
								MODIFY COLUMN file_submit_notify TINYINT(1) DEFAULT 0;

ALTER TABLE tl_lasbmt11_session MODIFY COLUMN status TINYINT(1);									 		 
	
ALTER TABLE tl_lasbmt11_user MODIFY COLUMN finished TINYINT(1);	

UPDATE lams_tool SET tool_version='20170101' WHERE tool_signature='lasbmt11';

----------------------Put all sql statements above here-------------------------

-- If there were no errors, commit and restore autocommit to on
COMMIT;
SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS=1;