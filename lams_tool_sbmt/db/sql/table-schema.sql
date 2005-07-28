alter table tl_lasbmt11_instruction_files drop foreign key FKA75538F9FC4BEA1;
alter table tl_lasbmt11_submission_details drop foreign key FK1411A53C630DDF64;
alter table tl_lasbmt11_session drop foreign key FKEC8C77C9FC4BEA1;
drop table if exists tl_lasbmt11_report;
drop table if exists tl_lasbmt11_content;
drop table if exists tl_lasbmt11_instruction_files;
drop table if exists tl_lasbmt11_submission_details;
drop table if exists tl_lasbmt11_session;
create table tl_lasbmt11_report (
   report_id bigint not null auto_increment,
   comments varchar(250),
   marks bigint,
   date_marks_released datetime,
   primary key (report_id)
);
create table tl_lasbmt11_content (
   content_id bigint not null,
   title varchar(64) not null,
   instruction text,
   define_later bit not null,
   run_offline bit not null,
   offline_instruction text,
   online_instruction text,
   run_offline_instruction text,
   content_in_use bit,
   lock_on_finished bit,
   primary key (content_id)
);
create table tl_lasbmt11_instruction_files (
   file_id bigint not null auto_increment,
   uuid bigint,
   version_id bigint,
   type varchar(20),
   content_id bigint,
   primary key (file_id)
);
create table tl_lasbmt11_submission_details (
   submission_id bigint not null auto_increment,
   filePath varchar(250),
   fileDescription varchar(250),
   date_of_submission datetime,
   uuid bigint,
   version_id bigint,
   user_id bigint,
   session_id bigint,
   primary key (submission_id)
);
create table tl_lasbmt11_session (
   session_id bigint not null auto_increment,
   status integer not null,
   content_id bigint,
   primary key (session_id)
);
alter table tl_lasbmt11_instruction_files add index FKA75538F9FC4BEA1 (content_id), add constraint FKA75538F9FC4BEA1 foreign key (content_id) references tl_lasbmt11_content (content_id);
alter table tl_lasbmt11_submission_details add index FK1411A53C630DDF64 (session_id), add constraint FK1411A53C630DDF64 foreign key (session_id) references tl_lasbmt11_session (session_id);
alter table tl_lasbmt11_session add index FKEC8C77C9FC4BEA1 (content_id), add constraint FKEC8C77C9FC4BEA1 foreign key (content_id) references tl_lasbmt11_content (content_id);
