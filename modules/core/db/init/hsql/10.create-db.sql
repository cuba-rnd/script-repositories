-- begin SCRIPTREPO_PERSISTENT_SCRIPT
create table SCRIPTREPO_PERSISTENT_SCRIPT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    DESCRIPTION varchar(255),
    RETURN_TYPE varchar(255),
    SOURCE_TEXT varchar(255),
    ENABLED boolean,
    SCRIPT_VERSION integer,
    --
    primary key (ID)
)^
-- end SCRIPTREPO_PERSISTENT_SCRIPT

-- begin SCRIPTREPO_PERSISTENT_SCRIPT_PARAMETER
create table SCRIPTREPO_PERSISTENT_SCRIPT_PARAMETER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARAM_ORDER integer not null,
    NAME varchar(255) not null,
    PARAMETER_TYPE varchar(255),
    PERSISTENT_SCRIPT_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end SCRIPTREPO_PERSISTENT_SCRIPT_PARAMETER
