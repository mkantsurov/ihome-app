CREATE TABLE databasechangeloglock
(
    ID          INT     NOT NULL,
    LOCKED      BOOLEAN NOT NULL,
    LOCKGRANTED TIMESTAMP WITHOUT TIME ZONE,
    LOCKEDBY    VARCHAR(255),
    CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID)
);

-- init liquibase stuff
CREATE TABLE databasechangelog
(
    ID            VARCHAR(255)                NOT NULL,
    AUTHOR        VARCHAR(255)                NOT NULL,
    FILENAME      VARCHAR(255)                NOT NULL,
    DATEEXECUTED  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ORDEREXECUTED INT                         NOT NULL,
    EXECTYPE      VARCHAR(10)                 NOT NULL,
    MD5SUM        VARCHAR(35),
    DESCRIPTION   VARCHAR(255),
    COMMENTS      VARCHAR(255),
    TAG           VARCHAR(255),
    LIQUIBASE     VARCHAR(20),
    CONTEXTS      VARCHAR(255),
    LABELS        VARCHAR(255),
    DEPLOYMENT_ID VARCHAR(10)
);


--Initialize primary structure

CREATE SEQUENCE settings_seq INCREMENT 1;
/* Build Table Structure */
CREATE TABLE settings
(
    id             BIGINT NOT NULL DEFAULT nextval('settings_seq'),
    settings_key   VARCHAR(256),
    settings_value TEXT,
    creation_date  TIMESTAMP WITHOUT TIME ZONE

) WITHOUT OIDS;

ALTER TABLE settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


CREATE SEQUENCE audit_log_entry_id_seq INCREMENT 1;

CREATE TABLE audit_log_entry
(
    id                BIGINT                      DEFAULT nextval('audit_log_entry_id_seq') NOT NULL,
    session_id        CHARACTER VARYING(255),
    client_session_id TEXT,
    log_action        INTEGER,
    processor_type    INTEGER,
    processor_id      BIGINT,
    obj_type          INTEGER,
    obj_id            BIGINT,
    parent_obj_type   INTEGER,
    parent_obj_id     BIGINT,
    created           TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    descr             CHARACTER VARYING(255),
    status_code       INTEGER
);
ALTER TABLE audit_log_entry
    ADD CONSTRAINT audit_log_entry_pkey PRIMARY KEY (id);

CREATE INDEX audit_log_session_id_idx
    ON audit_log_entry (session_id);
CREATE INDEX audit_log_client_session_id_idx
    ON audit_log_entry (client_session_id);
CREATE INDEX audit_log_log_action_idx
    ON audit_log_entry (log_action);
CREATE INDEX audit_log_processor_id_idx
    ON audit_log_entry (processor_id);
CREATE INDEX audit_log_processor_type_idx
    ON audit_log_entry (processor_type);
CREATE INDEX audit_log_parent_id_idx
    ON audit_log_entry (parent_obj_id);
CREATE INDEX audit_log_obj_id_idx
    ON audit_log_entry (obj_id);
CREATE INDEX audit_log_obj_type_idx
    ON audit_log_entry (obj_type);
CREATE INDEX audit_log_parent_type_idx
    ON audit_log_entry (parent_obj_type);
CREATE INDEX audit_log_created_idx
    ON audit_log_entry (created);

CREATE SEQUENCE controller_config_entry_id_seq INCREMENT 1;

CREATE TABLE controller_config_entry
(
    id         BIGINT DEFAULT nextval('controller_config_entry_id_seq') NOT NULL,
    ip_address VARCHAR(32)                                              NOT NULL,
    name       VARCHAR(255)                                             NOT NULL
);

ALTER TABLE controller_config_entry
    ADD CONSTRAINT controller_config_entry_pkey PRIMARY KEY (id);

CREATE SEQUENCE controller_port_config_entry_id_seq INCREMENT 1;

CREATE TABLE controller_port_config_entry
(
    id            BIGINT DEFAULT nextval('controller_port_config_entry_id_seq') NOT NULL,
    controller_id BIGINT                                                        NOT NULL,
    port_address  INTEGER                                                       NOT NULL,
    type          INTEGER                                                       NOT NULL,
    description   VARCHAR(255)                                                  NOT NULL
);

ALTER TABLE controller_port_config_entry
    ADD CONSTRAINT controller_port_config_entry_pkey PRIMARY KEY (id);

ALTER TABLE controller_port_config_entry
    ADD CONSTRAINT controller_port_config_entry_controller_fk FOREIGN KEY (controller_id) REFERENCES controller_config_entry (id);

CREATE INDEX controller_id_idx
    ON controller_port_config_entry (controller_id);


CREATE SEQUENCE module_config_entry_id_seq INCREMENT 1;

CREATE SEQUENCE module_group_entry_id_seq INCREMENT 1;
CREATE TABLE module_group_entry
(
    id       BIGINT                DEFAULT nextval('module_group_entry_id_seq') NOT NULL,
    name     VARCHAR(255) NOT NULL,
    priority INTEGER      NOT NULL DEFAULT 0
);

ALTER TABLE module_group_entry
    ADD CONSTRAINT module_group_entry_pkey PRIMARY KEY (id);

CREATE TABLE module_config_entry
(
    id       BIGINT                DEFAULT nextval('module_config_entry_id_seq') NOT NULL,
    mode     INTEGER      NOT NULL DEFAULT 1,
    name     VARCHAR(255) NOT NULL,
    group_id BIGINT       NOT NULL,
    display  BOOLEAN      NOT NULL DEFAULT TRUE,
    type     INTEGER      NOT NULL DEFAULT 0
);

ALTER TABLE module_config_entry
    ADD CONSTRAINT module_config_entry_pkey PRIMARY KEY (id);
CREATE INDEX group_id_idx
    ON module_config_entry (group_id);
ALTER TABLE module_config_entry
    ADD CONSTRAINT module_config_entry_group_fk FOREIGN KEY (group_id) REFERENCES module_group_entry (id);


CREATE SEQUENCE module_config_element_entry_id_seq INCREMENT 1;

CREATE TABLE module_config_element_entry
(
    id           BIGINT                DEFAULT nextval('module_config_element_entry_id_seq') NOT NULL,
    module_id    BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    type         INTEGER      NOT NULL,
    port         BIGINT       NOT NULL,
    display_mode INTEGER      NOT NULL DEFAULT 1
);

ALTER TABLE module_config_element_entry
    ADD CONSTRAINT module_config_element_entry_pkey PRIMARY KEY (id);
ALTER TABLE module_config_element_entry
    ADD CONSTRAINT module_config_element_entry_module_fk FOREIGN KEY (module_id) REFERENCES module_config_entry (id);


CREATE INDEX module_id_idx
    ON module_config_element_entry (module_id);


CREATE SEQUENCE module_property_entry_id_seq INCREMENT 1;
CREATE TABLE module_property_entry
(
    id           BIGINT           DEFAULT nextval('module_property_entry_id_seq') NOT NULL,
    module_id    BIGINT,
    key          INTEGER NOT NULL DEFAULT 0,
    long_value   BIGINT,
    string_value VARCHAR(255)
);

ALTER TABLE module_property_entry
    ADD CONSTRAINT module_property_entry_pkey PRIMARY KEY (id);
ALTER TABLE module_property_entry
    ADD CONSTRAINT module_property_entry_module_fk FOREIGN KEY (module_id) REFERENCES module_config_entry (id);


CREATE SEQUENCE measurements_log_entry_id_seq INCREMENT 1;
CREATE TABLE measurements_log_entry
(
    id                 BIGINT                      DEFAULT nextval('measurements_log_entry_id_seq') NOT NULL,
    created            TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    pressure           INTEGER NOT NULL            DEFAULT 0,
    outdoor_temp       INTEGER NOT NULL            DEFAULT 0,
    outdoor_humidity   INTEGER NOT NULL            DEFAULT 0,
    indoor_sf_temp     INTEGER NOT NULL            DEFAULT 0,
    indoor_sf_humidity INTEGER NOT NULL            DEFAULT 0,
    indoor_gf_temp     INTEGER NOT NULL            DEFAULT 0,
    garage_temp        INTEGER NOT NULL            DEFAULT 0,
    garage_humidity    INTEGER NOT NULL            DEFAULT 0,
    boiler_temp        INTEGER NOT NULL            DEFAULT 0,
    luminosity         INTEGER NOT NULL            DEFAULT 0,
    memory_heap_max    INTEGER NOT NULL            DEFAULT 0,
    memory_heap_used   INTEGER NOT NULL            DEFAULT 0,
    load_avg           INTEGER NOT NULL            DEFAULT 0
);

ALTER TABLE measurements_log_entry
    ADD CONSTRAINT measurements_log_entry_pkey PRIMARY KEY (id);

INSERT INTO settings (settings_key, settings_value)
VALUES ('DB_VER', 0);

UPDATE settings
SET settings_value = 4
WHERE settings_key = 'DB_VER';


INSERT INTO controller_config_entry (id, ip_address, name)
VALUES (1, '192.168.88.71', 'Garage Controller');
INSERT INTO controller_config_entry (id, ip_address, name)
VALUES (2, '192.168.88.72', 'Ground Flor Controller');
INSERT INTO controller_config_entry (id, ip_address, name)
VALUES (3, '192.168.88.73', 'Second Flor Controller');
INSERT INTO controller_config_entry (id, ip_address, name)
VALUES (4, '192.168.88.74', 'Ground Flor Controller#2');

SELECT setval('controller_config_entry_id_seq', (SELECT max(id)
                                                 FROM controller_config_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (1, 1, 27, 1, 'Garage Gate Power Source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (2, 1, 20, 1, 'Verandah Light Power Source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (3, 1, 18, 1, 'Garage light Power Source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (4, 1, 23, 1, 'Garage Ventilation Power Source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (5, 1, 19, 1, 'Porch light power source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (6, 1, 5, 3, 'Garage Temp Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (7, 1, 6, 2, 'Garage doors sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (8, 1, 4, 2, 'Garage gate sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (9, 1, 0, 4, 'Boiler Temperature Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (10, 1, 1, 4, 'Heat Water Recirculation Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (11, 1, 2, 3, 'Garage outdoor temperature/humidity sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (12, 1, 26, 1, 'Garage boiler pump power source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (13, 3, 23, 1, 'Second flor 220v PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (14, 3, 24, 1, 'Recuperator');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (15, 3, 7, 1, 'Wardrobe Light PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (16, 3, 0, 2, 'Wardrobe Light Switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (17, 3, 1, 2, 'Children room #1 Light Switch #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (18, 3, 2, 2, 'Bedroom Light Switch #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (19, 3, 3, 2, 'Children room #2 Light Switch #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (20, 3, 15, 2, 'Children room #1 Light Switch #2');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (21, 3, 16, 2, 'Bedroom# Light Switch #2');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (22, 3, 17, 2, 'Children room #2 Light Switch #2');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (23, 3, 8, 1, 'Children room #1 Light PS #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (24, 3, 9, 1, 'Bedroom Light PS #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (25, 3, 10, 1, 'Children room #2 Light PS #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (26, 3, 25, 5, 'Children room #1 Light PS #2');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (27, 3, 27, 5, 'Bedroom Light PS #2');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (29, 1, 3, 2, 'Garage Input Power Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (30, 2, 22, 1, 'First Flor 220v PS standby control relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (31, 2, 23, 1, 'First Flor 220v PS generic control relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (32, 2, 11, 1, 'Bath Room (1-st flor) Light PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (33, 2, 12, 1, 'Corridor Light PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (34, 2, 20, 2, 'Bath room (1-st floor)  movenment sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (35, 2, 21, 2, 'Corridor movenment sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (36, 3, 31, 6, 'Pressure humidity temp. sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (37, 2, 25, 5, 'Kitchen main light PS relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (38, 2, 27, 5, 'Living room main light PS relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (39, 2, 28, 5, 'Cabinet main light PS relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (40, 2, 15, 2, 'Kitchen main Light power switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (41, 2, 16, 2, 'Living room Light power switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (42, 2, 17, 2, 'Cabinet Light power switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (43, 3, 6, 2, 'Corridor 2-nd flor light power SW');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (44, 3, 11, 1, 'Corridor 2-nd flor light PS relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (45, 1, 24, 1, 'Garage 220v');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (46, 1, 28, 1, 'Garage Solar Pump Power Source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (47, 3, 18, 2, 'Bath room (2-nd floor)  movenment sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (48, 3, 26, 1, 'Bath Room (2-nd flor) Light PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (50, 3, 28, 1, 'Bath Room (2-nd flor) Ventilation PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (51, 1, 25, 1, 'Heat Water recirculation pump PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (52, 3, 32, 3, 'Bath room (2-nd floor) temperature/humidity sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (53, 3, 22, 1, 'Stairwell Light Control Relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (54, 3, 4, 2, 'Stairwell motion sensor (sf)');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (55, 3, 5, 2, 'Stairwell motion sensor (gf)');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (56, 1, 21, 1, 'Input Source Direct');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (57, 1, 22, 1, 'Input Source Converter');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (58, 1, 30, 7, 'Luminosity Sensor (Garage)');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (59, 1, 24, 1, 'Garage boiler pump#2 power source');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (60, 1, 28, 1, 'Solar Heating System PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (61, 3, 13, 1, 'Loft Light PS #1');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (62, 4, 7, 1, 'Automatic water tap control Relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (63, 4, 8, 1, 'Automatic water tap control Relay');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (64, 4, 11, 1, 'Floor heating pump');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (65, 4, 12, 1, 'Box room Light PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (66, 4, 13, 1, 'Water Pump PS');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (67, 4, 30, 4, 'Ground Floor Temperature Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (68, 4, 0, 2, 'Box room Motion Sensor');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (69, 2, 3, 2, 'Exterior Light power switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (70, 2, 2, 2, 'Garden Light power switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (71, 2, 1, 2, 'Kitchen Illumination of the working area switch');
INSERT INTO controller_port_config_entry (id, controller_id, port_address, type, description)
VALUES (72, 2, 26, 1, 'Kitchen Illumination of the working area PS relay');

SELECT setval('controller_port_config_entry_id_seq', (SELECT max(id)
                                                      FROM controller_port_config_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

INSERT INTO module_group_entry (id, name, priority)
VALUES (1, 'Garage', 8);
INSERT INTO module_group_entry (id, name, priority)
VALUES (2, 'Ground flor', 10);
INSERT INTO module_group_entry (id, name, priority)
VALUES (3, 'Second floor', 9);
INSERT INTO module_group_entry (id, name, priority)
VALUES (4, 'External Lighting', 7);
INSERT INTO module_group_entry (id, name, priority)
VALUES (5, 'Heating Controls', 6);

SELECT setval('module_group_entry_id_seq', (SELECT max(id)
                                            FROM module_group_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (1, 'Garage doors power control module', 1, 2, true, 1);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (2, 'Garage light control module', 1, 2, true, 3);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (3, 'Garage ventilation control module', 1, 2, true, 4);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (4, 'Exterior light  control module', 4, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (5, 'Garage heating system pump control module', 1, 1, true, 5);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (6, 'Garden light power control module', 4, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (7, 'Second Flor 220v PS control module', 3, 2, true, 8);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (8, 'Recuperator PS control module', 3, 2, true, 8);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (9, 'Wardrobe light PS control module', 3, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (10, 'Children room #1 Light PS control module #1', 3, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (11, 'Children room #1 Light PS control module #2', 3, 1, true, 7);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (12, 'Bedroom Light PS control module #1', 3, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (13, 'Bedroom Light PS control module #2', 3, 1, true, 7);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (14, 'Children room #2 Light PS control module #1', 3, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (16, 'First Flor 220v PS standby control module', 2, 1, true, 8);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (17, 'First Flor 220v PS generic control module', 2, 2, true, 8);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (18, 'Bath Room (1-st floor) Light Control module', 2, 2, true, 9);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (19, 'Corridor Light Control module', 2, 2, true, 9);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (20, 'Kitchen main Light Control module', 2, 1, true, 7);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (21, 'Living room Light Control module', 2, 1, true, 7);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (22, 'Cabinet Light Control module', 2, 1, true, 7);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (23, 'Corridor 2-nd flor light control module', 3, 2, true, 13);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (24, 'Bath Room (2-nd floor) Light Control module', 3, 2, true, 9);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (26, 'Bath Room (2-nd floor) Ventilation Control module', 3, 2, true, 10);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (27, 'Heat Water recirculation pump control module', 1, 1, true, 14);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (28, 'Stairwell light control module', 3, 2, true, 13);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (29, 'Input Source Direct Power Control Module', 1, 2, true, 11);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (30, 'Input Source Converter Power Control Module', 1, 2, true, 12);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (31, 'Garage heating system pump control module#2', 1, 1, true, 5);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (32, 'Garage Solar System PS module', 1, 1, true, 1);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (33, 'Loft light PS control module', 3, 1, true, 6);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (34, 'Automatic water tap control module', 2, 1, true, 1);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (35, 'Floor heating pump control module', 2, 1, true, 1);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (36, 'Box room Light control module', 2, 2, true, 9);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (37, 'Water Pump PS control module', 2, 1, true, 1);
INSERT INTO module_config_entry (id, name, group_id, mode, display, type)
VALUES (38, 'Kitchen Illumination of the working area Control module', 2, 1, true, 6);

SELECT setval('module_config_entry_id_seq', (SELECT max(id)
                                             FROM module_config_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (1, 1, 'Garage dors power control relay', 1, 1, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (2, 2, 'Garage light power control relay', 1, 3, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (5, 2, 'Garage doors sensor', 3, 7, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (6, 2, 'Garage gate sensor', 3, 8, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (7, 3, 'Garage ventilation power control relay', 1, 4, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (8, 3, 'Garage temperature sensor', 5, 6, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (9, 4, 'Exterior light power control relay', 1, 2, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (10, 5, 'Heating system power control relay', 1, 12, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (11, 5, 'Boiler temperature sensor', 4, 9, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (12, 3, 'Outdoor temperature/humidity', 5, 11, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (13, 6, 'Garden light power control relay', 1, 5, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (14, 7, 'Second Flor 220v PS control relay', 1, 13, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (15, 8, 'Recuperator PS control relay', 1, 14, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (16, 9, 'Wardrobe light control relay', 1, 15, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (17, 9, 'Wardrobe light switch', 2, 16, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (18, 10, 'Children room #1 Light Control Relay #1', 1, 23, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (19, 10, 'Children room #1 Light Light Switch #1', 2, 17, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (20, 11, 'Children room #1 Light Control Relay #2', 6, 26, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (21, 11, 'Children room #1 Light Light Switch #2', 2, 20, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (22, 12, 'Bedroom Light Control Relay #1', 1, 24, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (23, 12, 'Bedroom Light Light Switch #1', 2, 18, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (24, 13, 'Bedroom Light Control Relay #2', 6, 27, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (25, 13, 'Bedroom Light Light Switch #2', 2, 21, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (26, 14, 'Children room #2 Light Control Relay #1', 1, 25, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (27, 14, 'Children room #2 Light Light Switch #1', 2, 19, 0);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (30, 7, 'Input Power Sensor', 2, 29, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (31, 8, 'Input Power Sensor', 2, 29, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (32, 16, 'First Flor 220v PS standby control relay', 1, 30, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (33, 17, 'Second Flor 220v PS generic control relay', 1, 31, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (34, 16, 'Input Power Sensor', 2, 29, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (35, 17, 'Input Power Sensor', 2, 29, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (36, 18, 'Bath Room (1-st floor) Light Control Relay', 1, 32, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (37, 18, 'Bath room (1-st floor)  motion sensor', 2, 34, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (38, 19, 'Corridor Light Control Relay', 1, 33, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (39, 19, 'Corridor motion sensor', 2, 35, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (40, 8, 'Pressure humidity temp. sensor', 7, 36, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (41, 20, 'Kitchen main Light Control Relay', 6, 37, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (42, 21, 'Living room Light Control Relay', 6, 38, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (43, 31, 'Pressure humidity temp. sensor', 7, 36, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (44, 22, 'Living room Light Control Relay', 6, 39, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (45, 20, 'Kitchen main Light power switch', 2, 40, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (46, 21, 'Living room Light power switch', 2, 41, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (47, 22, 'Cabinet Light power switch', 2, 42, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (48, 23, 'Corridor 2-nd flor motion sensor', 2, 43, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (49, 23, 'Corridor 2-nd flor light PS relay', 1, 44, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (50, 24, 'Bath room (2-nd floor)  motion sensor', 2, 47, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (51, 24, 'Bath Room (2-nd floor) Light Control Relay', 1, 48, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (52, 26, 'Bath Room (2-nd floor) Ventilation Control Relay', 1, 50, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (53, 27, 'Heat water recirculation pump PS', 1, 51, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (54, 27, 'Heat water recirculation temp sensor', 4, 10, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (55, 26, 'Bath room (2-nd floor) temperature/humidity sensor', 5, 52, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (56, 28, 'Stairwell motion sensor (sf)', 2, 54, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (57, 28, 'Stairwell motion sensor (gf)', 2, 55, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (58, 28, 'Stairwell Light Control Relay', 1, 53, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (59, 26, 'Bath room (2-nd floor) motion sensor', 2, 47, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (60, 26, 'Pressure humidity temp. sensor', 7, 36, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (61, 29, 'Input Source Direct', 1, 56, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (62, 30, 'Input Source Converter', 1, 57, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (63, 29, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (64, 30, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (65, 2, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (66, 4, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (67, 6, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (68, 18, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (69, 19, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (70, 23, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (71, 24, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (72, 28, 'Luminosity Sensor', 8, 58, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (73, 31, 'Heating system power control relay', 1, 59, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (74, 31, 'Boiler temperature sensor', 4, 9, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (75, 5, 'Ground Floor Temperature Sensor', 4, 67, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (76, 32, 'Garage Solar System PS control relay', 1, 60, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (77, 33, 'Loft Light PS ', 1, 61, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (78, 34, 'Automatic water tap control Relay', 1, 62, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (79, 34, 'Automatic water tap control Relay', 1, 63, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (80, 35, 'Floor heating pump control Relay', 1, 64, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (81, 35, 'Ground Floor Temperature Sensor', 4, 67, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (82, 35, 'Boiler temperature sensor', 4, 9, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (83, 36, 'Box room Light control Motion Sensor', 2, 68, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (84, 36, 'Box room Light Control Relay', 1, 65, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (85, 37, 'Water Pump PS Control Relay', 1, 66, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (86, 4, 'Exterior light power control switch', 2, 69, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (87, 6, 'Garden light power control switch', 2, 70, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (88, 38, 'Kitchen Illumination of the working area power control switch', 2, 71, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (89, 38, 'Kitchen Illumination of the working area Control Relay', 1, 72, 1);
INSERT INTO module_config_element_entry (id, module_id, name, type, port, display_mode)
VALUES (90, 27, 'Boiler temperature sensor', 4, 9, 1);

INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (1, 28, 1, 1); --stears light ttl
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (2, 26, 7, 30); --interval checking humidity bath room 2-nd floor
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (3, 26, 6, 55); --bath room (2nd floor) humidity sensor ID
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (4, 26, 5, 60); --indoor humidity sensor ID
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (5, 26, 8, 59); --bath room 2nd floor motion sensor
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (6, 23, 1, 1); --corridor 2-nd floor light ttl
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (7, 36, 1, 3); --box room, 1-nd floor light ttl
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (8, 5, 11, 2200); --heating indoor temperature first floor
INSERT INTO module_property_entry (id, module_id, key, long_value)
VALUES (9, 31, 11, 2400); --heating indoor temperature second floor

SELECT setval('module_config_element_entry_id_seq', (SELECT max(id)
                                                     FROM module_config_element_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;


SELECT setval('controller_port_config_entry_id_seq', (SELECT max(id)
                                                      FROM controller_port_config_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('module_config_entry_id_seq', (SELECT max(id)
                                             FROM module_config_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;


SELECT setval('module_group_entry_id_seq', (SELECT max(id)
                                            FROM module_group_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;

SELECT setval('module_property_entry_id_seq', (SELECT max(id)
                                               FROM module_property_entry))
INTO TEMP TABLE tmp;
DROP TABLE tmp;
