--
-- Table structure for table `event`
--

SET character_set_client = utf8;
CREATE TABLE `events` (
  `id` bigint(20) NOT NULL auto_increment,
  `epc` varchar(128) NOT NULL,
  `eventType` varchar(16) NOT NULL,
  `bizStep` varchar(128) NOT NULL,
  `eventTime` timestamp NOT NULL,
  `serviceType` varchar(32) NOT NULL,
  `serviceAddress` varchar(128) NOT NULL,
  `owner` varchar(128) NOT NULL,
  PRIMARY KEY  (`id`),
  INDEX (`epc`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
