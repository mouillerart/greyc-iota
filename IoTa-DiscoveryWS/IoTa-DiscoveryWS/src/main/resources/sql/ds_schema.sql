-- MySQL dump 10.11
--
-- Host: localhost    Database: ds_repository
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `event` (
  `ID` bigint(20) NOT NULL auto_increment,
  `epc` varchar(1023) NOT NULL,
  `partner_ID` bigint(20) NOT NULL REFERENCES `partner`(`ID`),
  /*`eventtopublish` bigint(20) REFERENCES `eventtopublish`(`ID`),*/
  `epcClass` varchar(1023) NOT NULL,
  `event_time_stamp` timestamp NOT NULL,
  `source_time_stamp` timestamp NOT NULL,
  `bizStep` varchar(1023) NOT NULL,
  `event_type` varchar(8) NOT NULL CHECK (`event_type` IN ('OBJECT','VOID')),
  PRIMARY KEY  (`ID`),
  INDEX (`epc`)
) ENGINE=MyISAM AUTO_INCREMENT=839254 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;


DROP TABLE IF EXISTS `voc_BizStep`;
CREATE TABLE `voc_BizStep` (
`ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL
);

DROP TABLE IF EXISTS `voc_EPCClass`;
CREATE TABLE `voc_EPCClass` (
`ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
`uri` varchar(1023) NOT NULL
);

DROP TABLE IF EXISTS `event_EPCs`;
CREATE TABLE `event_EPCs` (
`ID` bigint PRIMARY KEY auto_increment,
`uri` varchar(1023) NOT NULL,
INDEX (`uri`)
);


--
-- Table structure for table `eventtopublish`
--

DROP TABLE IF EXISTS `eventtopublish`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `eventtopublish` (
  `ID` bigint(20) NOT NULL auto_increment,
  `event` bigint(20),
  `lastupdate` timestamp NOT NULL,
  PRIMARY KEY  (`ID`),
  FOREIGN KEY (`event`) REFERENCES `event`(`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=421113 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


DROP TRIGGER IF EXISTS `eventtopublish_delete_trigger`;

/*
delimiter |

CREATE TRIGGER eventtopublish_delete_trigger BEFORE DELETE ON eventtopublish
  FOR EACH ROW BEGIN
    UPDATE event SET eventtopublish=null WHERE eventtopublish=OLD.event;
  END;

|
delimiter ;

*/

--
-- Dumping data for table `eventtopublish`
--

LOCK TABLES `eventtopublish` WRITE;
/*!40000 ALTER TABLE `eventtopublish` DISABLE KEYS */;
/*!40000 ALTER TABLE `eventtopublish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partner`
--

DROP TABLE IF EXISTS `partner`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `partner` (
  `ID` bigint(20) NOT NULL auto_increment,
  `partnerID` varchar(30) NOT NULL ,
  `serviceType` varchar(30) NOT NULL,
  `serviceAddress` varchar(100) NOT NULL,
  `date` timestamp NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `partner`
--

LOCK TABLES `partner` WRITE;
/*!40000 ALTER TABLE `partner` DISABLE KEYS */;
INSERT INTO `partner` VALUES (1,'superadmin','ds','superadmin','2008-07-31',1);
INSERT INTO `partner` VALUES (2,'anonymous','epcis','none','2008-07-31',2);
/*!40000 ALTER TABLE `partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sc`
--

DROP TABLE IF EXISTS `sc`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sc` (
  `ID` bigint(20) NOT NULL auto_increment,
  `scID` varchar(30) NOT NULL,
  `partner_ID` bigint(20) NOT NULL REFERENCES `partner`(`id`),
  `bizStepPolicy` varchar(8) DEFAULT 'ACCEPT' CHECK (`bizStepPolicy` IN ('DROP','ACCEPT')),
  `eventTimePolicy` varchar(8) DEFAULT 'ACCEPT' CHECK (`eventTimePolicy` IN ('DROP','ACCEPT')),
  `epcClassPolicy` varchar(8) DEFAULT 'ACCEPT' CHECK (`eventClassPolicy` IN ('DROP','ACCEPT')),
  `epcsPolicy` varchar(8) DEFAULT 'ACCEPT' CHECK (`epcsPolicy` IN ('DROP','ACCEPT')),
  `date` timestamp NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


DROP TABLE IF EXISTS `sc_BizStep_Restriction`;
CREATE TABLE `sc_BizStep_Restriction` (
  `ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
  `scID` bigint NOT NULL REFERENCES `sc` (`id`),
  `bizStep` bigint NOT NULL REFERENCES `voc_BizStep` (`id`)
);

DROP TABLE IF EXISTS `sc_EPCClass_Restriction`;
CREATE TABLE `sc_EPCClass_Restriction` (
  `ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
  `scID` bigint NOT NULL REFERENCES `sc` (`id`),
  `EPCClass` bigint NOT NULL REFERENCES `voc_EPCClass` (`id`)
);

DROP TABLE IF EXISTS `sc_EPCs_Restriction`;
CREATE TABLE `sc_EPCs_Restriction` (
  `ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
  `scID` bigint NOT NULL REFERENCES `sc` (`id`),
  `epc` varchar(1023) NOT NULL
);

DROP TABLE IF EXISTS `sc_EventTime_Restriction`;
CREATE TABLE `sc_EventTime_Restriction` (
  `ID` bigint PRIMARY KEY auto_increment, -- id auto_increment
  `scID` bigint NOT NULL REFERENCES `sc` (`id`),
  `firstEventTime` timestamp default 0,
  `lastEventTime` timestamp default 0
);

/*
  `firstEventTime` timestamp default '0000-00-00 00:00:00',
  `lastEventTime` timestamp default '0000-00-00 00:00:00'
*/

--
-- Table structure for table `scassociation`
--

DROP TABLE IF EXISTS `scassociation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `scassociation` (
  `ID` bigint(20) NOT NULL auto_increment,
  `sc_ID` bigint(20) NOT NULL,
  `partner_ID` bigint(20) NOT NULL REFERENCES `partner`(`id`),
  `date` timestamp NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user` (
  `ID` bigint(20) NOT NULL auto_increment,
  `userID` varchar(30) NOT NULL,
  `passwd` varchar(30) NOT NULL,
  `login` varchar(30) NOT NULL,
  `partner_ID` bigint(20) NOT NULL REFERENCES `partner`(`id`),
  `date` timestamp NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'superadmin','superadmin','superadmin',1,'2008-07-31');
INSERT INTO `user` VALUES (2,'anonymous','anonymous','anonymous',2,'2008-07-31');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-06-17 10:07:29
