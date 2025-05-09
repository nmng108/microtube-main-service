-- MySQL
/**
  * @author nmng108
  */

drop database MICROTUBE;
CREATE DATABASE IF NOT EXISTS MICROTUBE;
USE MICROTUBE;

# DROP TABLE USER;
# DROP TABLE CHANNEL;
# DROP TABLE VIDEO;
# ALTER TABLE CHANNEL DROP CONSTRAINT fn_channel_user;
# ALTER TABLE VIDEO DROP CONSTRAINT fn_video_channel;

CREATE TABLE USER
(
    ID              BIGINT       NOT NULL AUTO_INCREMENT,
    USERNAME        VARCHAR(20)  NOT NULL UNIQUE,
    PASSWORD        VARCHAR(200) NOT NULL,
    NAME            VARCHAR(255) NOT NULL,
    EMAIL           VARCHAR(100) NOT NULL,
    PHONE_NUMBER    VARCHAR(20),
    ADDITIONAL_INFO TEXT,
    AVATAR          VARCHAR(150) COMMENT 'Path to uploaded image file',
    CREATED_BY      BIGINT,
    CREATED_AT      DATETIME     NOT NULL,
    MODIFIED_BY     BIGINT,
    MODIFIED_AT     DATETIME,
    DELETED_BY      BIGINT,
    DELETED_AT      DATETIME,
    PRIMARY KEY (ID)
);
CREATE INDEX IDX_USER_USERNAME ON USER (USERNAME);
CREATE INDEX IDX_USER_EMAIL ON USER (EMAIL(50));
CREATE INDEX IDX_USER_PHONE ON USER (PHONE_NUMBER);
CREATE INDEX IDX_USER_CREATED_BY ON USER (CREATED_BY);
CREATE INDEX IDX_USER_MODIFIED_BY ON USER (MODIFIED_BY);
CREATE INDEX IDX_USER_DELETED_BY ON USER (DELETED_BY);

### Access control tables ###

CREATE TABLE SYS_ENTITY
(
    ID          INT         NOT NULL AUTO_INCREMENT,
    CODE        VARCHAR(30) NOT NULL,
    NAME        VARCHAR(50) NOT NULL,
    DESCRIPTION VARCHAR(200),
    CREATED_BY  BIGINT,
    CREATED_AT  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY BIGINT,
    MODIFIED_AT DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY  BIGINT,
    DELETED_AT  DATETIME,
    PRIMARY KEY (ID)
);
CREATE INDEX IDX_SYS_ENTITY_CODE ON SYS_ENTITY (CODE);

CREATE TABLE SYS_ACTION
(
    ID          INT         NOT NULL AUTO_INCREMENT,
    CODE        VARCHAR(30) NOT NULL,
    NAME        VARCHAR(50) NOT NULL,
    DESCRIPTION VARCHAR(200),
    CREATED_BY  BIGINT,
    CREATED_AT  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY BIGINT,
    MODIFIED_AT DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY  BIGINT,
    DELETED_AT  DATETIME,
    PRIMARY KEY (ID)
);
CREATE INDEX IDX_SYS_ACTION_CODE ON SYS_ACTION (CODE);

CREATE TABLE PERMISSION
(
    ID            INT         NOT NULL AUTO_INCREMENT,
    CODE          VARCHAR(30) NOT NULL UNIQUE,
    SYS_ENTITY_ID INT,
    SYS_ACTION_ID INT,
    DESCRIPTION   VARCHAR(200),
    CREATED_BY    BIGINT,
    CREATED_AT    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY   BIGINT,
    MODIFIED_AT   DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY    BIGINT,
    DELETED_AT    DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE PERMISSION
    ADD CONSTRAINT FK_PERMISSION_TO_SYS_ENTITY FOREIGN KEY (SYS_ENTITY_ID) REFERENCES SYS_ENTITY (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PERMISSION
    ADD CONSTRAINT FK_PERMISSION_TO_SYS_ACTION FOREIGN KEY (SYS_ACTION_ID) REFERENCES SYS_ACTION (ID) ON UPDATE CASCADE ON DELETE CASCADE;
CREATE INDEX IDX_PERMISSION_CODE ON PERMISSION (CODE);

CREATE TABLE PERMISSION_GROUP
(
    ID          INT         NOT NULL AUTO_INCREMENT,
    CODE        VARCHAR(30) NOT NULL UNIQUE,
    NAME        VARCHAR(50) NOT NULL,
    DESCRIPTION VARCHAR(200),
    CREATED_BY  BIGINT,
    CREATED_AT  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY BIGINT,
    MODIFIED_AT DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY  BIGINT,
    DELETED_AT  DATETIME,
    PRIMARY KEY (ID)
) COMMENT 'This table can also be known for presenting user''s roles';
CREATE INDEX IDX_PERMISSION_GROUP_CODE ON PERMISSION_GROUP (CODE);

CREATE TABLE PERMISSION_GROUP_RELATION_PERMISSION
(
    PERMISSION_ID       INT      NOT NULL,
    PERMISSION_GROUP_ID INT      NOT NULL,
    DESCRIPTION         VARCHAR(200),
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY         BIGINT,
    MODIFIED_AT         DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY          BIGINT,
    DELETED_AT          DATETIME,
    PRIMARY KEY (PERMISSION_ID, PERMISSION_GROUP_ID)
);
ALTER TABLE PERMISSION_GROUP_RELATION_PERMISSION
    ADD CONSTRAINT FK_PERMISSION_GROUP_RELATION_PERMISSION_TO_PERMISSION FOREIGN KEY (PERMISSION_ID) REFERENCES PERMISSION (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PERMISSION_GROUP_RELATION_PERMISSION
    ADD CONSTRAINT FK_PERMISSION_GROUP_RELATION_PERMISSION_TO_PERMISSION_GROUP FOREIGN KEY (PERMISSION_GROUP_ID) REFERENCES PERMISSION_GROUP (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE PERMISSION_RELATION_USER
(
    USER_ID       BIGINT   NOT NULL,
    PERMISSION_ID INT      NOT NULL,
    DESCRIPTION   VARCHAR(200),
    CREATED_BY    BIGINT,
    CREATED_AT    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY   BIGINT,
    MODIFIED_AT   DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY    BIGINT,
    DELETED_AT    DATETIME,
    PRIMARY KEY (USER_ID, PERMISSION_ID)
) COMMENT 'The table contains permissions that users are exclusively assigned, which may not exist in any permission group';
ALTER TABLE PERMISSION_RELATION_USER
    ADD CONSTRAINT FK_PERMISSION_RELATION_USER_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PERMISSION_RELATION_USER
    ADD CONSTRAINT FK_PERMISSION_RELATION_USER_TO_PERMISSION FOREIGN KEY (PERMISSION_ID) REFERENCES PERMISSION (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE PERMISSION_GROUP_RELATION_USER
(
    USER_ID             BIGINT   NOT NULL,
    PERMISSION_GROUP_ID INT      NOT NULL,
    DESCRIPTION         VARCHAR(200),
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY         BIGINT,
    MODIFIED_AT         DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY          BIGINT,
    DELETED_AT          DATETIME,
    PRIMARY KEY (USER_ID, PERMISSION_GROUP_ID)
) COMMENT 'The table contains permissions that users are exclusively assigned, which may not exist in any permission group';
ALTER TABLE PERMISSION_GROUP_RELATION_USER
    ADD CONSTRAINT FK_PERMISSION_GROUP_RELATION_USER_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PERMISSION_GROUP_RELATION_USER
    ADD CONSTRAINT FK_PERMISSION_GROUP_RELATION_USER_TO_PERMISSION FOREIGN KEY (PERMISSION_GROUP_ID) REFERENCES PERMISSION_GROUP (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE PERMISSION_EXCLUSION
(
    USER_ID       BIGINT   NOT NULL,
    PERMISSION_ID INT      NOT NULL,
    DESCRIPTION   VARCHAR(200),
    CREATED_BY    BIGINT,
    CREATED_AT    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFIED_BY   BIGINT,
    MODIFIED_AT   DATETIME ON UPDATE CURRENT_TIMESTAMP,
    DELETED_BY    BIGINT,
    DELETED_AT    DATETIME,
    PRIMARY KEY (USER_ID, PERMISSION_ID)
) COMMENT 'The table contains permissions that users are prevented to do';
ALTER TABLE PERMISSION_EXCLUSION
    ADD CONSTRAINT FK_PERMISSION_EXCLUSION_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PERMISSION_EXCLUSION
    ADD CONSTRAINT FK_PERMISSION_EXCLUSION_TO_PERMISSION FOREIGN KEY (PERMISSION_ID) REFERENCES PERMISSION (ID) ON UPDATE CASCADE ON DELETE CASCADE;

######

CREATE TABLE CHANNEL
(
    ID                 BIGINT          NOT NULL AUTO_INCREMENT,
    NAME               VARCHAR(255)    NOT NULL,
    PATHNAME           VARCHAR(50)     NOT NULL UNIQUE COMMENT 'Alternative of sequential ID, which is preferred for rendering on end-user side.',
    DESCRIPTION        TEXT,
    AVATAR             VARCHAR(150) COMMENT 'Path to uploaded image file; default to user''s avatar.',
    SUBSCRIPTION_COUNT BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Equals to total of referencing CHANNEL_SUBSCRIPTION records',
    VIDEO_COUNT        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    USER_ID            BIGINT          NOT NULL,
    CREATED_BY         BIGINT          NOT NULL,
    CREATED_AT         DATETIME        NOT NULL,
    MODIFIED_BY        BIGINT,
    MODIFIED_AT        DATETIME,
    DELETED_BY         BIGINT,
    DELETED_AT         DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE CHANNEL
    ADD CONSTRAINT FK_CHANNEL_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE CHANNEL
    ADD FULLTEXT INDEX INDEX_FT_CHANNEL_NAME_DESC (PATHNAME, NAME, DESCRIPTION);
CREATE INDEX IDX_CHANNEL_CREATED_BY ON CHANNEL (CREATED_BY);
CREATE INDEX IDX_CHANNEL_MODIFIED_BY ON CHANNEL (MODIFIED_BY);
CREATE INDEX IDX_CHANNEL_DELETED_BY ON CHANNEL (DELETED_BY);

CREATE TABLE CHANNEL_SUBSCRIPTION
(
    CHANNEL_ID BIGINT NOT NULL,
    USER_ID    BIGINT NOT NULL,
    CREATED_AT DATETIME,
    PRIMARY KEY (CHANNEL_ID, USER_ID)
);
ALTER TABLE CHANNEL_SUBSCRIPTION
    ADD CONSTRAINT FK_CHANNEL_SUBSCRIPTION_TO_CHANNEL FOREIGN KEY (CHANNEL_ID) REFERENCES CHANNEL (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE CHANNEL_SUBSCRIPTION
    ADD CONSTRAINT FK_CHANNEL_SUBSCRIPTION_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE VIDEO
(
    ID                BIGINT          NOT NULL AUTO_INCREMENT,
    CODE              VARCHAR(20)     NOT NULL UNIQUE, -- alternative of sequential ID, which is preferred for rendering on end-user side
    TITLE             VARCHAR(500)    NOT NULL,
    DESCRIPTION       TEXT,
    VISIBILITY        TINYINT         NOT NULL,
    THUMBNAIL         VARCHAR(150),
    ORIGINAL_FILENAME VARCHAR(150),
    TEMP_FILEPATH     VARCHAR(1000),
    DEST_FILEPATH     VARCHAR(1000),
    RESOLUTION        VARCHAR(150) COMMENT 'A comma-separated list of resolution names. Ex: 720p,1080p',
    STATUS            TINYINT         NOT NULL,
    ALLOW_COMMENT     TINYINT COMMENT '0: No; 1: Yes',
    VIEW_COUNT        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    LIKE_COUNT        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    DISLIKE_COUNT     BIGINT UNSIGNED NOT NULL DEFAULT 0,
    CHANNEL_ID        BIGINT          NOT NULL,
    CREATED_BY        BIGINT          NOT NULL,
    CREATED_AT        DATETIME        NOT NULL,
    MODIFIED_BY       BIGINT,
    MODIFIED_AT       DATETIME,
    DELETED_BY        BIGINT,
    DELETED_AT        DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE VIDEO
    ADD CONSTRAINT FK_VIDEO_TO_CHANNEL FOREIGN KEY (CHANNEL_ID) REFERENCES CHANNEL (ID) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE VIDEO
    ADD FULLTEXT INDEX INDEX_FT_VIDEO_NAME_DESC (TITLE, DESCRIPTION);
CREATE INDEX IDX_VIDEO_CREATED_BY ON VIDEO (CREATED_BY);
CREATE INDEX IDX_VIDEO_MODIFIED_BY ON VIDEO (MODIFIED_BY);
CREATE INDEX IDX_VIDEO_DELETED_BY ON VIDEO (DELETED_BY);

CREATE TABLE USER_RELATION_VIDEO
(
    USER_ID        BIGINT   NOT NULL,
    VIDEO_ID       BIGINT   NOT NULL,
    PAUSE_POSITION BIGINT,
    REACTION       TINYINT COMMENT 'NULL: no reaction; 1: Like; 2: Unlike',
    CREATED_AT     DATETIME NOT NULL,
    MODIFIED_AT    DATETIME,
    PRIMARY KEY (USER_ID, VIDEO_ID)
);
ALTER TABLE USER_RELATION_VIDEO
    ADD CONSTRAINT FK_USER_RELATION_VIDEO_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE USER_RELATION_VIDEO
    ADD CONSTRAINT FK_USER_RELATION_VIDEO_TO_VIDEO FOREIGN KEY (VIDEO_ID) REFERENCES VIDEO (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE WATCH_HISTORY
(
    ID             BIGINT   NOT NULL AUTO_INCREMENT,
    USER_ID        BIGINT   NOT NULL,
    VIDEO_ID       BIGINT   NOT NULL,
    PAUSE_POSITION BIGINT,
    CREATED_AT     DATETIME NOT NULL,
    MODIFIED_AT    DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE WATCH_HISTORY
    ADD CONSTRAINT FK_WATCH_HISTORY_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE RESTRICT;
CREATE INDEX IDX_WATCH_HISTORY_VIDEO_ID ON WATCH_HISTORY (VIDEO_ID);

CREATE TABLE COMMENT
(
    ID            BIGINT          NOT NULL AUTO_INCREMENT,
    USER_ID       BIGINT          NOT NULL,
    VIDEO_ID      BIGINT          NOT NULL,
    PARENT_ID     BIGINT,
    LEVEL         TINYINT         NOT NULL DEFAULT 1,
    CONTENT       TEXT,
    LIKE_COUNT    BIGINT UNSIGNED NOT NULL DEFAULT 0,
    DISLIKE_COUNT BIGINT UNSIGNED NOT NULL DEFAULT 0,
    CREATED_AT    DATETIME        NOT NULL,
    MODIFIED_BY   BIGINT,
    MODIFIED_AT   DATETIME,
    DELETED_BY    BIGINT,
    DELETED_AT    DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE COMMENT
    ADD CONSTRAINT FK_COMMENT_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE NO ACTION;
ALTER TABLE COMMENT
    ADD CONSTRAINT FK_COMMENT_TO_VIDEO FOREIGN KEY (VIDEO_ID) REFERENCES VIDEO (ID) ON UPDATE CASCADE ON DELETE RESTRICT;
CREATE INDEX IDX_COMMENT_PARENT_ID ON COMMENT (PARENT_ID);
CREATE INDEX IDX_COMMENT_MODIFIED_BY ON COMMENT (MODIFIED_BY);
CREATE INDEX IDX_COMMENT_DELETED_BY ON COMMENT (DELETED_BY);

CREATE TABLE COMMENT_REACTION
(
    USER_ID     BIGINT   NOT NULL,
    COMMENT_ID  BIGINT   NOT NULL,
    REACTION    TINYINT COMMENT '1: Like; 2: Unlike',
    CREATED_AT  DATETIME NOT NULL,
    MODIFIED_AT DATETIME,
    PRIMARY KEY (USER_ID, COMMENT_ID)
);
ALTER TABLE COMMENT_REACTION
    ADD CONSTRAINT FK_COMMENT_REACTION_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE COMMENT_REACTION
    ADD CONSTRAINT FK_COMMENT_REACTION_TO_COMMENT FOREIGN KEY (COMMENT_ID) REFERENCES VIDEO (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE PLAYLIST
(
    ID          BIGINT          NOT NULL AUTO_INCREMENT,
    CODE        VARCHAR(20)     NOT NULL,
    NAME        VARCHAR(400)    NOT NULL,
    DESCRIPTION TEXT,
    VISIBILITY  TINYINT         NOT NULL,
    USER_ID     BIGINT          NOT NULL,
    VIEW_COUNT  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    CREATED_BY  BIGINT          NOT NULL,
    CREATED_AT  DATETIME        NOT NULL,
    MODIFIED_BY BIGINT,
    MODIFIED_AT DATETIME,
    PRIMARY KEY (ID)
);
ALTER TABLE PLAYLIST
    ADD CONSTRAINT FK_PLAYLIST_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
CREATE INDEX IDX_PLAYLIST_CREATED_BY ON PLAYLIST (CREATED_BY);
CREATE INDEX IDX_PLAYLIST_MODIFIED_BY ON PLAYLIST (MODIFIED_BY);

CREATE TABLE PLAYLIST_RELATION_VIDEO
(
    PLAYLIST_ID BIGINT    NOT NULL,
    VIDEO_ID    BIGINT    NOT NULL,
    ORDINAL     MEDIUMINT NOT NULL,
    CREATED_AT  DATETIME  NOT NULL,
    PRIMARY KEY (PLAYLIST_ID, VIDEO_ID)
);
ALTER TABLE PLAYLIST_RELATION_VIDEO
    ADD CONSTRAINT FK_PLAYLIST_RELATION_VIDEO_TO_PLAYLIST FOREIGN KEY (PLAYLIST_ID) REFERENCES PLAYLIST (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE PLAYLIST_RELATION_VIDEO
    ADD CONSTRAINT FK_PLAYLIST_RELATION_VIDEO_TO_VIDEO FOREIGN KEY (VIDEO_ID) REFERENCES VIDEO (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE SAVED_PLAYLIST
(
    USER_ID     BIGINT   NOT NULL,
    PLAYLIST_ID BIGINT   NOT NULL,
    CREATED_AT  DATETIME NOT NULL
);
ALTER TABLE SAVED_PLAYLIST
    ADD CONSTRAINT FK_SAVED_PLAYLIST_TO_USER FOREIGN KEY (USER_ID) REFERENCES USER (ID) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE SAVED_PLAYLIST
    ADD CONSTRAINT FK_SAVED_PLAYLIST_TO_PLAYLIST FOREIGN KEY (PLAYLIST_ID) REFERENCES PLAYLIST (ID) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE NOTIFICATION_TYPE
(
    ID               BIGINT       NOT NULL AUTO_INCREMENT,
    NAME             VARCHAR(400) NOT NULL,
    MESSAGE_TEMPLATE VARCHAR(2000),
    ENTITY_TYPE      VARCHAR(50),
    ACTION           VARCHAR(50),
    CREATED_BY       BIGINT,
    CREATED_AT       DATETIME     NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE NOTIFICATION
(
    ID         BIGINT    NOT NULL AUTO_INCREMENT,
    MESSAGE    VARCHAR(4000),
    IMAGE_URL  VARCHAR(500),
    URL        VARCHAR(500),
    ENTITY_ID  VARCHAR(30) COMMENT 'Store either numeric or string ID value',
    ACTOR_ID   VARCHAR(50) COMMENT 'USER.ID or CHANNEL.ID, depend on the notification type',
    TYPE_ID    BIGINT    NOT NULL,
    CREATED_BY BIGINT,
    CREATED_AT DATETIME NOT NULL,
    PRIMARY KEY (ID)
);
CREATE INDEX IDX_NOTIFICATION_TYPE_ID ON NOTIFICATION (TYPE_ID);

CREATE TABLE NOTIFICATION_RECIPIENT
(
    NOTIFICATION_ID BIGINT    NOT NULL,
    RECIPIENT_ID    BIGINT    NOT NULL COMMENT 'USER.ID or CHANNEL.ID, depend on the notification type',
    IS_SEEN         TINYINT,
    IS_READ         TINYINT,
    CREATED_AT      DATETIME NOT NULL,
    READ_AT         DATETIME,
    PRIMARY KEY (NOTIFICATION_ID, RECIPIENT_ID)
);
CREATE INDEX IDX_NOTIFICATION_RECIPIENT_NOTIFICATION_ID ON NOTIFICATION_RECIPIENT (NOTIFICATION_ID);
CREATE INDEX IDX_NOTIFICATION_RECIPIENT_RECIPIENT_ID ON NOTIFICATION_RECIPIENT (RECIPIENT_ID);
