-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: thfh_admin
-- ------------------------------------------------------
-- Server version	5.7.16-log

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(100) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','$2a$10$ZXx81OW5MV7lkTmGtYvwHOzfDdKlNgHp0k/S1J.h78FYCzgTJNttC','系统管理员',NULL,NULL,'2025-01-10 16:50:55',1,'2025-01-07 23:32:01','2025-01-07 23:32:01');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `description` text,
  `industry` varchar(100) DEFAULT NULL,
  `scale` varchar(50) DEFAULT NULL,
  `website` varchar(200) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'阳光助残科技有限公司','/images/companies/sunshine.png','致力于为残疾人提供就业机会和技术支持的科技公司','互联网/科技','100-499人','www.sunshine-tech.com','北京市朝阳区阳光大厦',1,'2025-01-07 23:33:03','2025-01-07 23:33:03');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `cover_image` varchar(255) NOT NULL,
  `teacher_id` bigint(20) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `points_price` int(11) DEFAULT NULL,
  `total_hours` int(11) NOT NULL,
  `status` varchar(20) NOT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `materials` text,
  `like_count` int(11) DEFAULT '0',
  `favorite_count` int(11) DEFAULT '0',
  `student_count` int(11) DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'手工艺品制作入门','本课程将教授学员基础的手工艺品制作技巧，包括材料选择、工具使用、制作技巧等。','/images/courses/handcraft.jpg',1,99.00,1000,20,'PUBLISHED','/videos/courses/handcraft-intro.mp4','教材：《手工艺品制作基础》\n工具清单：剪刀、胶水、彩纸等',0,0,0,1,'2025-01-07 23:32:33','2025-01-07 23:32:33');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `company_id` bigint(20) NOT NULL,
  `location` varchar(200) NOT NULL,
  `salary_min` decimal(10,2) NOT NULL,
  `salary_max` decimal(10,2) NOT NULL,
  `requirements` text NOT NULL,
  `benefits` text NOT NULL,
  `disability_support` text NOT NULL,
  `contact_person` varchar(50) NOT NULL,
  `contact_phone` varchar(50) DEFAULT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `view_count` int(11) DEFAULT '0',
  `apply_count` int(11) DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `job_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job`
--

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;
INSERT INTO `job` VALUES (1,'网页设计师','负责公司产品的界面设计，交互设计，视觉设计等工作',1,'北京市朝阳区',8000.00,15000.00,'1. 美术、设计相关专业优先\n2. 熟练使用设计软件\n3. 有良好的艺术审美能力\n4. 有网页设计经验优先','1. 五险一金\n2. 带薪年假\n3. 节日福利\n4. 定期体检','1. 无障碍办公环境\n2. 灵活工作时间\n3. 专业辅助设备\n4. 工作导师制度','张经理','13800138000','hr@sunshine-tech.com','PUBLISHED',0,0,1,'2025-01-07 23:33:12','2025-01-07 23:33:12');
/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `points_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `student_id` bigint(20) NOT NULL,
  `points` int(11) NOT NULL,
  `type` varchar(20) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `points_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_record`
--

LOCK TABLES `points_record` WRITE;
/*!40000 ALTER TABLE `points_record` DISABLE KEYS */;
INSERT INTO `points_record` VALUES (1,2,100,'ADMIN_ADJUST','新用户注册奖励','2025-01-07 23:32:52');
/*!40000 ALTER TABLE `points_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `rating` int(11) NOT NULL,
  `content` text NOT NULL,
  `images` text,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,'COURSE',1,2,5,'老师讲解非常详细，课程内容很实用，收获很大！','/images/reviews/course1.jpg,/images/reviews/course2.jpg',1,'2025-01-07 23:33:23','2025-01-07 23:33:23');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thfh_order`
--

DROP TABLE IF EXISTS `thfh_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thfh_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户ID',
  `work_id` bigint(20) NOT NULL COMMENT '作品ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` varchar(20) NOT NULL COMMENT '订单状态：PENDING-待付款，PAID-已付款，SHIPPED-已发货，COMPLETED-已完成，CANCELLED-已取消',
  `shipping_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `shipping_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `shipping_address` varchar(255) NOT NULL COMMENT '收货地址',
  `logistics_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(50) DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_work_id` (`work_id`),
  CONSTRAINT `FK46852imgnbngqyga11io0iv14` FOREIGN KEY (`work_id`) REFERENCES `work` (`id`),
  CONSTRAINT `FKf3mck1g7tahifolnypo0bfpkw` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thfh_order`
--

LOCK TABLES `thfh_order` WRITE;
/*!40000 ALTER TABLE `thfh_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `thfh_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(100) DEFAULT NULL,
  `user_type` varchar(20) NOT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `introduction` varchar(500) DEFAULT NULL,
  `qualification` varchar(500) DEFAULT NULL,
  `speciality` varchar(500) DEFAULT NULL,
  `disability` varchar(100) DEFAULT NULL,
  `points` int(11) DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `last_login_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'teacher1','$2a$10$X/hX4qvWzxZ3V3hdyuR9NOeKqZ1Ws47nZwbZBw1ZOyFzc.6.Y3MOi','张教员','TEACHER','13800138001','teacher1@thfh.com',NULL,'从事特殊教育工作10年，具有丰富的教学经验','特殊教育高级教师资格证，心理咨询师证书','美术设计，手工艺品制作',NULL,0,1,NULL,'2025-01-07 23:32:13','2025-01-07 23:32:13'),(2,'student1','$2a$10$X/hX4qvWzxZ3V3hdyuR9NOeKqZ1Ws47nZwbZBw1ZOyFzc.6.Y3MOi','李学员','STUDENT','13900139001','student1@thfh.com',NULL,'热爱学习，对手工艺品制作很感兴趣',NULL,NULL,'听力障碍',100,1,NULL,'2025-01-07 23:32:22','2025-01-07 23:32:22');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work`
--

DROP TABLE IF EXISTS `work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `cover_image` varchar(255) NOT NULL,
  `student_id` bigint(20) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `images` text,
  `video` varchar(255) DEFAULT NULL,
  `materials` text,
  `like_count` int(11) DEFAULT '0',
  `view_count` int(11) DEFAULT '0',
  `sale_count` int(11) DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `work_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work`
--

LOCK TABLES `work` WRITE;
/*!40000 ALTER TABLE `work` DISABLE KEYS */;
INSERT INTO `work` VALUES (1,'手工编织包','这是一个用天然材料编织的环保手提包，采用传统工艺制作。','/images/works/bag.jpg',2,299.00,'ON_SALE','/images/works/bag1.jpg,/images/works/bag2.jpg,/images/works/bag3.jpg',NULL,'材料：天然麻绳、棉布\n尺寸：30x20x10cm',0,0,0,1,'2025-01-07 23:32:43','2025-01-07 23:32:43');
/*!40000 ALTER TABLE `work` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-17 18:10:47
