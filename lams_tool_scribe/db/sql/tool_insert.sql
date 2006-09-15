-- CVS ID: $Id$
 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
help_url,
language_file,
create_date_time
)
VALUES
(
'lascrb11',
'scribeService',
'Scribe',
'Scribe',
'scribe',
'1.1',
NULL,
NULL,
0,
2,
1,
'tool/lascrb11/learning.do?mode=learner',
'tool/lascrb11/learning.do?mode=author',
'tool/lascrb11/learning.do?mode=teacher',
'tool/lascrb11/authoring.do',
'tool/lascrb11/monitoring.do',
'tool/lascrb11/authoring.do?mode=teacher',
'tool/lascrb11/exportPortfolio?mode=learner',
'tool/lascrb11/exportPortfolio?mode=teacher',
'tool/lascrb11/contribute.do',
'tool/lascrb11/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lascrb11',
'org.lamsfoundation.lams.tool.scribe.ApplicationResources',
NOW()
)
