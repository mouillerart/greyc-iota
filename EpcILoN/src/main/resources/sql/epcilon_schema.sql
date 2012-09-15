BEGIN;

SET storage_engine=INNODB;

-- ---------------------------------------------
-- Aggregation Events
-- ---------------------------------------------
DROP TABLE IF EXISTS EventToPublish;

CREATE TABLE `EventToPublish` (
`id` bigint PRIMARY KEY auto_increment,
`eventTime` timestamp NOT NULL,
`epc` varchar(1023) DEFAULT NULL,
`bizStep` varchar(1023) DEFAULT NULL,
`eventType` varchar(1023) DEFAULT NULL,
`eventClass` varchar(1023) DEFAULT NULL,
`lastUpdate` timestamp NOT NULL
);

COMMIT;

