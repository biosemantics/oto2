CREATE TABLE IF NOT EXISTS `otosteps_collection` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `taxongroup` varchar(100) NOT NULL,
  `secret` varchar(100) NOT NULL,
  `lastretrieved` TIMESTAMP NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_term` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `original_term` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `removed` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `collection` bigint(20) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_context` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `collection` bigint(20) unsigned NOT NULL,
  `source` varchar(100) NOT NULL,
  `text` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0;

CREATE TABLE IF NOT EXISTS `otosteps_ontology` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `iri` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `acronym` varchar(100) NOT NULL,
  `browse_url` varchar(100) NULL DEFAULT NULL,
  `collection` bigint(20) DEFAULT -1,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `collection_name` (`collection`, `name`),
  UNIQUE KEY `collection_acronym` (`collection`, `acronym`),
  UNIQUE KEY `collection_iri` (`collection`, `iri`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontology_taxongroup` (
  `ontology` bigint(20) unsigned NOT NULL,
  `taxongroup` varchar(100) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ontology`, `taxongroup`),
  UNIQUE KEY `id` (`ontology`, `taxongroup`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologyclasssubmission` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`term` BIGINT(20) UNSIGNED NOT NULL,
	`submission_term` VARCHAR(100) NOT NULL,
	`ontology` BIGINT(20) UNSIGNED NOT NULL,
	`class_iri` VARCHAR(100) DEFAULT NULL,
	`definition` text NULL DEFAULT NULL,
  	`source` TEXT NULL DEFAULT NULL,
	`sample_sentence` TEXT NULL DEFAULT NULL,  
	`entity` TINYINT(1) NULL DEFAULT '0',
	`quality` TINYINT(1) NULL DEFAULT '0',
	`user` VARCHAR(100) NULL DEFAULT NULL,
  	`lastupdated` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologyclasssubmission_synonym` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ontologyclasssubmission` BIGINT(20) UNSIGNED NOT NULL,
	`synonym` VARCHAR(100) NOT NULL,
  	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologyclasssubmission_partof` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ontologyclasssubmission` BIGINT(20) UNSIGNED NOT NULL,
	`partof` VARCHAR(100) NOT NULL,
  	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologyclasssubmission_superclass` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ontologyclasssubmission` BIGINT(20) UNSIGNED NOT NULL,
	`superclass` VARCHAR(100) NOT NULL,
  	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE `otosteps_ontologysynonymsubmission` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`term` BIGINT(20) UNSIGNED NOT NULL,
	`submission_term` VARCHAR(100) NOT NULL,
	`ontology` BIGINT(20) UNSIGNED NOT NULL,
	`class_iri` VARCHAR(100) NOT NULL,
	`synonyms` TEXT NULL DEFAULT NULL,
	`source` TEXT NULL DEFAULT NULL,
	`sample_sentence` TEXT NULL DEFAULT NULL,
	`entity` TINYINT(1) NULL DEFAULT '0',
	`quality` TINYINT(1) NULL DEFAULT '0',
	`user` VARCHAR(100) NULL DEFAULT NULL,
	`lastupdated` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologysynonymsubmission_synonym` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`ontologysynonymsubmission` BIGINT(20) UNSIGNED NOT NULL,
	`synonym` VARCHAR(100) NOT NULL,
  	`created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologyclasssubmission_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ontologyclasssubmission` bigint(20) unsigned NOT NULL,
  `status` bigint(20) unsigned NOT NULL,
  `iri` varchar(100) NOT NULL,
  `lastupdated` TIMESTAMP NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `otosteps_ontologysynonymsubmission_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ontologysynonymsubmission` bigint(20) unsigned NOT NULL,
  `status` bigint(20) unsigned NOT NULL,
  `iri` varchar(100) NOT NULL,
  `lastupdated` TIMESTAMP NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (1, 'http://purl.obolibrary.org/obo/pato.owl', 'Phenotypic Quality Ontology', 'PATO', 'http://www.ontobee.org/browser/index.php?o=PATO', -1, '2015-04-09 16:29:38');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (2, 'http://purl.obolibrary.org/obo/ro.owl', 'The OBO Relations Ontology', 'RO', 'http://www.ontobee.org/browser/index.php?o=RO', -1, '2015-04-09 16:31:00');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (3, 'http://purl.obolibrary.org/obo/bspo.owl', 'Biological Spatial Ontology', 'BSPO', 'http://www.ontobee.org/browser/index.php?o=BSPO', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (4, 'http://purl.obolibrary.org/obo/hao.owl', 'Hymenoptera Anatomy Ontology', 'HAO', 'http://www.ontobee.org/browser/index.php?o=HAO', -1, '2015-04-09 16:32:03');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (5, 'http://purl.obolibrary.org/obo/po.owl', 'Plant Ontology', 'PO', 'http://www.ontobee.org/browser/index.php?o=PO', -1, '2015-04-09 16:33:10');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (6, 'http://purl.obolibrary.org/obo/poro.owl', 'Porifera Ontology', 'PORO', 'http://www.ontobee.org/browser/index.php?o=PORO', -1, '2015-04-09 16:33:49');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (7, 'http://purl.obolibrary.org/obo/uberon.owl', 'Uber Anatomy Ontology', 'UBERON', 'http://www.ontobee.org/browser/index.php?o=UBERON', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (8, 'http://purl.obolibrary.org/obo/caro/src/caro.obo.owl', 'Common Anatomy Reference Ontology', 'CARO', 'http://www.ontobee.org/browser/index.php?o=CARO', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (9, 'http://purl.obolibrary.org/obo/cl.owl', 'Cell Ontology', 'CL', 'http://www.ontobee.org/browser/index.php?o=CL', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (10, 'http://purl.obolibrary.org/obo/envo.owl', 'Environment Ontology', 'ENVO', 'http://www.ontobee.org/browser/index.php?o=ENVO', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (11, 'http://purl.obolibrary.org/obo/go.owl', 'Gene Ontology', 'GO', 'http://www.ontobee.org/browser/index.php?o=GO', -1, '2015-04-09 16:31:29');
INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (12, 'http://purl.obolibrary.org/obo/chebi.owl', 'Chemical Entities of Biological Interest Ontology', 'CHEBI', 'http://www.ontobee.org/browser/index.php?o=CHEBI', -1, '2015-04-09 16:31:29');


INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'ALGAE');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'CNIDARIA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'FOSSIL');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'GASTROPODS');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'HYMENOPTERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'PLANT');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (1, 'PORIFERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'ALGAE');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'CNIDARIA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'FOSSIL');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'GASTROPODS');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'HYMENOPTERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'PLANT');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (2, 'PORIFERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'ALGAE');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'CNIDARIA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'FOSSIL');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'GASTROPODS');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'HYMENOPTERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'PLANT');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (3, 'PORIFERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (4, 'HYMENOPTERA');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (5, 'PLANT');
INSERT INTO `otosteps2`.`otosteps_ontology_taxongroup` (`ontology`, `taxongroup`) VALUES (6, 'PORIFERA');

INSERT INTO `otosteps_status` (`id`, `name`, `created`) VALUES (1, 'accepted', '2015-04-21 10:57:55');
INSERT INTO `otosteps_status` (`id`, `name`, `created`) VALUES (2, 'pending', '2015-04-21 10:58:05');
INSERT INTO `otosteps_status` (`id`, `name`, `created`) VALUES (3, 'rejected', '2015-04-21 10:58:13');

--INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (14, 'http://purl.obolibrary.org/obo/bfo.owl', 'Basic Formal Ontology', 'BFO', 'http://www.ontobee.org/browser/index.php?o=BFO', -1, '2015-04-09 16:31:29');
--INSERT INTO `otosteps_ontology` (`id`, `iri`, `name`, `acronym`, `browse_url`, `collection`, `created`) VALUES (13, 'http://purl.obolibrary.org/obo/pr.owl', 'Protein Ontology', 'PR', 'http://www.ontobee.org/browser/index.php?o=PR', -1, '2015-04-09 16:31:29');