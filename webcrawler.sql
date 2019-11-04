/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 50557
 Source Host           : localhost:3306
 Source Schema         : webcrawler

 Target Server Type    : MySQL
 Target Server Version : 50557
 File Encoding         : 65001

 Date: 04/11/2019 19:56:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apibaseconf
-- ----------------------------
DROP TABLE IF EXISTS `apibaseconf`;
CREATE TABLE `apibaseconf`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webId` bigint(20) NOT NULL DEFAULT 0,
  `prefix` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `inputXpath` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `submitXpath` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `infoLinkXpath` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用于指定返回查询页面上数据链接的位置，用于帮助链接收集器收集链接\n当此值为空时，运行收集器的默认行为',
  `payloadXpath` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'format:\nxpath,name',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of apibaseconf
-- ----------------------------
INSERT INTO `apibaseconf` VALUES (2, 124, 'http://10.24.13.223:8080/hbky/privateFileManager/grwpgl', '//*[@id=\"searchtext\"]', '//div[@class=\"searchIcon\"]', '//div[@id=\"allwenjian\"]//a[@href]', '//div[@id=\"allwenjian\"]//div[@class=\"filename\"],title');

-- ----------------------------
-- Table structure for current
-- ----------------------------
DROP TABLE IF EXISTS `current`;
CREATE TABLE `current`  (
  `webId` int(20) UNSIGNED NOT NULL,
  `round` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `M1status` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'inactive',
  `M2status` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'inactive',
  `M3status` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'inactive',
  `M4status` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'inactive',
  `SampleData_sum` int(11) NOT NULL DEFAULT 0,
  `run` bigint(20) NOT NULL DEFAULT 0,
  INDEX `round`(`round`(255)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of current
-- ----------------------------
INSERT INTO `current` VALUES (123, '1', 'stop', 'done', 'done', 'done', 245945, 0);
INSERT INTO `current` VALUES (127, '1', 'done', 'done', 'done', 'active', 223, 0);

-- ----------------------------
-- Table structure for estimate
-- ----------------------------
DROP TABLE IF EXISTS `estimate`;
CREATE TABLE `estimate`  (
  `contentLocation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `startWord` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `estiId` int(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `linksXpath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `pagesInfoId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `contentXpath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `rateBar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `walkTimes` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `querySend` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `pid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  PRIMARY KEY (`estiId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1129 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of estimate
-- ----------------------------
INSERT INTO `estimate` VALUES ('text', '', 122, 'article-list contentDisplay', '', 'list05', '', '', '', '500', 'get', '');
INSERT INTO `estimate` VALUES ('', '', 123, '', '', '', '0', 'stop', '100.00%', '1', '', '0');
INSERT INTO `estimate` VALUES ('', '', 124, '', '', '', '', '', '', '', '', '');
INSERT INTO `estimate` VALUES ('', '', 125, '', '', '', '', '', '', '', '', '');
INSERT INTO `estimate` VALUES ('text', '', 126, 'article-list contentDisplay', '', '', '', 'stop', '', '', 'get', '');
INSERT INTO `estimate` VALUES ('text', '的', 127, 'result-title', '', 'Content', '9223372036854775807', 'start', '01.00%', '200', 'get', '9480');
INSERT INTO `estimate` VALUES ('text', '公司', 128, 'list_resultlist', '', 'showtabinfo', '0', 'start', '75.60%', '500', 'get', '16796');
INSERT INTO `estimate` VALUES ('', '', 131, '', '', '', '0', 'stop', '100.0%', '1', '', '13984');
INSERT INTO `estimate` VALUES ('', '', 137, '', '', '', '', '', '', '', '', '');
INSERT INTO `estimate` VALUES ('', '', 138, '', '', '', '', '', '', '', '', '');
INSERT INTO `estimate` VALUES ('', '', 144, '', '', '', '163733', 'start', '33.33%', '3', '', '20112');

-- ----------------------------
-- Table structure for extraconf
-- ----------------------------
DROP TABLE IF EXISTS `extraconf`;
CREATE TABLE `extraconf`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webId` bigint(20) NOT NULL DEFAULT 0,
  `userNameXpath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `passwordXpath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `userName` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `password` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `loginUrl` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `submitXpath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `threadNum` int(11) NOT NULL DEFAULT 5,
  `timeout` bigint(20) NOT NULL DEFAULT 3000,
  `charset` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `databaseSize` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extraconf
-- ----------------------------
INSERT INTO `extraconf` VALUES (8, 122, '', '', '', '', '', '', 5, 3000, 'UTF-8', 0);
INSERT INTO `extraconf` VALUES (9, 123, 'txtUserName', 'txtPassword', '431200000000', 'aaaaaa', 'http://ai.inspur.com/login', 'btnLogin', 5, 3000, 'UTF-8', 13);
INSERT INTO `extraconf` VALUES (10, 124, '//*[@id=\"j_username\"]', '//*[@id=\"j_password\"]', 'zongyb', 'abing201!2', 'http://10.24.13.223:8080/hbky/index.jsp#', '//*[@id=\"submit_btn\"]', 5, 3000, 'UTF-8', 0);
INSERT INTO `extraconf` VALUES (11, 125, '//*[@id=\"j_username\"]', '//*[@id=\"j_password\"]', 'zongyb', 'abing201!2', 'http://10.24.13.223:8080/hbky/index.jsp#', '//*[@id=\"submit_btn\"]', 20, 30000, 'UTF-8', 0);
INSERT INTO `extraconf` VALUES (12, 126, 'txtUserName', 'txtPassword', '431200000000', 'aaaaaa', 'http://ai.inspur.com/login', 'btnLogin', 20, 8000, 'UTF-8', 249951);
INSERT INTO `extraconf` VALUES (13, 127, '', '', '', '', '', '', 5, 3000, 'utf-8', 0);
INSERT INTO `extraconf` VALUES (14, 144, 'txtUserName', 'txtPassword', '431200000000', 'aaaaaa', 'http://ai.inspur.com/Login', 'btnLogin', 10, 8000, 'UTF-8', 0);

-- ----------------------------
-- Table structure for formsbymd5
-- ----------------------------
DROP TABLE IF EXISTS `formsbymd5`;
CREATE TABLE `formsbymd5`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `formMd5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of formsbymd5
-- ----------------------------
INSERT INTO `formsbymd5` VALUES (53, '<��=�m�&e���Uwm');

-- ----------------------------
-- Table structure for jsonbase
-- ----------------------------
DROP TABLE IF EXISTS `jsonbase`;
CREATE TABLE `jsonbase`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webId` bigint(20) NOT NULL DEFAULT 0,
  `pageSize` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `totalAddress` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '总页数在json response中的位置',
  `contentAddress` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'if value is an empty string, the root is cotent address',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of jsonbase
-- ----------------------------
INSERT INTO `jsonbase` VALUES (2, 126, '1000', '/d/total', '/d/rows');
INSERT INTO `jsonbase` VALUES (3, 144, '1000', '/d/total', '/d/rows');

-- ----------------------------
-- Table structure for jsonbaseconf
-- ----------------------------
DROP TABLE IF EXISTS `jsonbaseconf`;
CREATE TABLE `jsonbaseconf`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webId` bigint(20) NOT NULL DEFAULT 0,
  `prefix` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramQuery` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramPage` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `pageStrategy` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `constString` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `totalAddress` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '总页数在json response中的位置',
  `contentAddress` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'if value is an empty string, the root is cotent address',
  `linkRule` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `payloadRule` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of jsonbaseconf
-- ----------------------------
INSERT INTO `jsonbaseconf` VALUES (1, 125, 'http://10.24.13.223:8080/hbky/search/getResult?', 'keyword', 'pageIndex', '1,1', 'type=0&searchtime=0', '/0/sum', '', '[http://10.24.13.223:8080/hbky/lucene/wjdownload?path=]+/path+[&filename=]+/filename+[&fileid=]+/fileid+[&category=]+/category', '/content');
INSERT INTO `jsonbaseconf` VALUES (2, 131, '', '', '', '', '', '', '', '', '');

-- ----------------------------
-- Table structure for pattern
-- ----------------------------
DROP TABLE IF EXISTS `pattern`;
CREATE TABLE `pattern`  (
  `webId` int(20) UNSIGNED NOT NULL DEFAULT 0,
  `patternName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'fulltext',
  `xpath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `id` int(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for pattern_structed
-- ----------------------------
DROP TABLE IF EXISTS `pattern_structed`;
CREATE TABLE `pattern_structed`  (
  `webId` int(11) UNSIGNED NOT NULL,
  `patternName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '主键，模板名称，每个网站都有两个默认模板fulltext和table',
  `xpath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '该模板在该网站页面中的xpath，多个xpath可以用##分割',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `headerXPath` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `webId`(`webId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of pattern_structed
-- ----------------------------
INSERT INTO `pattern_structed` VALUES (123, 'a', '/html/body/div[3]/div/div[1]/div[1]/div[1]/div/table', '(a+b)*(c+d)', 'formula', '/', 2);
INSERT INTO `pattern_structed` VALUES (123, 'b', '/html/body/div[3]/div/div[1]/div[2]/div[1]/div/table', '(a+b)*(c+d)', 'formula', '/', 3);
INSERT INTO `pattern_structed` VALUES (123, 'c', '/html/body/div[3]/div/div[1]/div[1]/div[2]/div/table', '(a+b)*(c+d)', 'formula', '/', 4);
INSERT INTO `pattern_structed` VALUES (123, 'd', '/html/body/div[3]/div/div[1]/div[2]/div[2]/table', '(a+b)*(c+d)', 'formula', '/', 5);
INSERT INTO `pattern_structed` VALUES (123, 'subpage_data', 'pcObj', 'pcObj', 'json', '/subpage', 6);

-- ----------------------------
-- Table structure for queryparam
-- ----------------------------
DROP TABLE IF EXISTS `queryparam`;
CREATE TABLE `queryparam`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `webId` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `dataParamList` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `N` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `C` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `webId`(`webId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of queryparam
-- ----------------------------
INSERT INTO `queryparam` VALUES (1, 123, '一般贫困户,低保户,五保户,低保贫困户;因病,因残,因学,因灾,缺土地,缺水', '24', '6');
INSERT INTO `queryparam` VALUES (2, 144, '一般贫困户,低保户,五保户,低保户贫困户,一般农户,五保贫困户;因病,因残,因学,因灾,缺土地,缺水,缺技术,缺劳力,缺资金,交通条件落后,自身发展动力不足,其他,未填写', '78', '14');

-- ----------------------------
-- Table structure for requesttable
-- ----------------------------
DROP TABLE IF EXISTS `requesttable`;
CREATE TABLE `requesttable`  (
  `requestID` bigint(20) NOT NULL AUTO_INCREMENT,
  `requestName` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `requestDesc` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `createdTime` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`requestID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of requesttable
-- ----------------------------
INSERT INTO `requesttable` VALUES (2, '111', '', '2019-10-28 22:58:38');

-- ----------------------------
-- Table structure for sense
-- ----------------------------
DROP TABLE IF EXISTS `sense`;
CREATE TABLE `sense`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `homeUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `targetUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sense
-- ----------------------------
INSERT INTO `sense` VALUES (158, 'http://www.cecic.com.cn/', 'http://www.cecic.com.cn/');

-- ----------------------------
-- Table structure for sensestate
-- ----------------------------
DROP TABLE IF EXISTS `sensestate`;
CREATE TABLE `sensestate`  (
  `id` int(20) NOT NULL,
  `allLinks` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `trueLinks` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sensestate
-- ----------------------------
INSERT INTO `sensestate` VALUES (122, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (123, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (124, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (125, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (126, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (127, '0', '1', 'stop');
INSERT INTO `sensestate` VALUES (128, '0', '0', 'stop');
INSERT INTO `sensestate` VALUES (131, '0', '0', 'stop');

-- ----------------------------
-- Table structure for status
-- ----------------------------
DROP TABLE IF EXISTS `status`;
CREATE TABLE `status`  (
  `webId` int(20) UNSIGNED NOT NULL,
  `statusId` int(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `round` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `type` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'query',
  `fLinkNum` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `sLinkNum` int(11) UNSIGNED NOT NULL DEFAULT 0,
  INDEX `round`(`round`(255)) USING BTREE,
  INDEX `statusId`(`statusId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7136 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of status
-- ----------------------------
INSERT INTO `status` VALUES (126, 7130, '0', 'info', 0, 245945);
INSERT INTO `status` VALUES (126, 7131, '0', 'query', 0, 250);
INSERT INTO `status` VALUES (126, 7132, '1', 'info', 0, 0);
INSERT INTO `status` VALUES (126, 7133, '1', 'query', 0, 0);
INSERT INTO `status` VALUES (127, 7134, '1', 'info', 4, 223);
INSERT INTO `status` VALUES (127, 7135, '1', 'query', 0, 257);

-- ----------------------------
-- Table structure for structedparam
-- ----------------------------
DROP TABLE IF EXISTS `structedparam`;
CREATE TABLE `structedparam`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `webId` int(11) UNSIGNED NOT NULL DEFAULT 0,
  `iframeNav` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `navValue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `iframeCon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `searchButton` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `resultRow` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `nextPageXPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `pageNumXPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `iframeSubParam` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `arrow` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramList` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramValueList` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of structedparam
-- ----------------------------
INSERT INTO `structedparam` VALUES (2, 123, 'ifmNav', '贫困县', 'ifmCon', 'btnSearch', 'Aaa003', '/html/body/div[3]/div/div[2]/table/tbody/tr/td[8]/a', '/html/body/div[3]/div/div[2]/table/tbody/tr/td[5]/input', 'Aaa003', 'combo-arrow', '/', '_easyui_combobox_i8_0,_easyui_combobox_i8_1,_easyui_combobox_i8_2,_easyui_combobox_i8_3,_easyui_combobox_i8_4,_easyui_combobox_i8_5');
INSERT INTO `structedparam` VALUES (3, 144, '', '', '', '', '', '', '', '', '', '', NULL);

-- ----------------------------
-- Table structure for urlbaseconf
-- ----------------------------
DROP TABLE IF EXISTS `urlbaseconf`;
CREATE TABLE `urlbaseconf`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webId` bigint(20) NOT NULL DEFAULT 0,
  `prefix` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramQuery` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramPage` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `startPageNum` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramList` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `paramValueList` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of urlbaseconf
-- ----------------------------
INSERT INTO `urlbaseconf` VALUES (4, 122, 'http://www.zhaoan.gov.cn/cms/siteresource/search.shtml?', 'key', 'page', '1,1', 'searchSiteId,siteId,pageName', '60427348114130001,60427348114130001,quickSiteSearch');
INSERT INTO `urlbaseconf` VALUES (5, 126, 'http://ai.inspur.com/Archive/PoorFamilyList-GetPoorFamilyData', 'poorproperty,poorcause,planOutPoor,realname,name6,basicArea,txtYear,Aad105,isHelp,isHelpPeople,isImmigrant,isPlan,AreaType,Aah006,Aad003,condition,membercondition,orders,sorts,poorFamilyType', 'pagenumber', '1,1', 'isNull,pagesize', '0,1000');
INSERT INTO `urlbaseconf` VALUES (6, 127, 'http://www.cecic.com.cn/g1313.aspx?', 'word', 'pindex', '1,1', 'sort', 'd');
INSERT INTO `urlbaseconf` VALUES (7, 128, 'http://www.ccen.info/plus/search.php?', 'keyword', 'PageNo', '1,1', 'searchtype,channeltype,orderby,kwtype,pagesize,typeid', 'title,0,,0,10,0');
INSERT INTO `urlbaseconf` VALUES (8, 123, '', '', '', '', '', '');
INSERT INTO `urlbaseconf` VALUES (9, 144, 'http://ai.inspur.com/Archive/PoorFamilyList-GetPoorFamilyData', 'poorproperty,poorcause,planOutPoor,realname,name6,basicArea,txtYear,Aad105,isHelp,isHelpPeople,isImmigrant,isPlan,AreaType,Aah006,Aad003,condition,membercondition,orders,sorts,poorFamilyType', 'pagenumber', '1,1', 'isNull,pagesize', '0,30');

-- ----------------------------
-- Table structure for website
-- ----------------------------
DROP TABLE IF EXISTS `website`;
CREATE TABLE `website`  (
  `webId` int(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `webName` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `indexUrl` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `workFile` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `runningMode` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `driver` tinyint(4) NOT NULL DEFAULT 0,
  `usable` tinyint(4) NOT NULL DEFAULT 0,
  `createtime` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `creator` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'task creator',
  `base` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:url based\n1:api based',
  PRIMARY KEY (`webId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of website
-- ----------------------------
INSERT INTO `website` VALUES (122, '诏安县政府官网', 'http://www.zhaoan.gov.cn/cms/html/zaxrmzf/index.html', 'E:/test01/zhaoan', 'unstructed', 0, 1, '2019-03-16 19:25:56', '', 0);
INSERT INTO `website` VALUES (123, '扶贫', 'http://ai.inspur.com/Main/Archive', 'D:/crawler', 'structed', 2, 1, '2019-03-19 14:15:51', '', 1);
INSERT INTO `website` VALUES (124, '网盘爬取', 'http://10.24.13.223:8080/hbky/index.jsp#', '/Users/cwc/Desktop/tencent/data-crawling/pan', 'unstructed', 0, 1, '2019-03-24 14:48:30', '', 1);
INSERT INTO `website` VALUES (125, '网盘全文检索', 'http://10.24.13.223:8080/hbky/index.jsp#', '/Users/cwc/Desktop/tencent/data-crawling/pan', 'unstructed', 0, 1, '', '', 2);
INSERT INTO `website` VALUES (126, '扶贫测试', 'http://ai.inspur.com/Main/Archive', 'D:/table/provty', 'structed', 2, 1, '2019-03-19 14:15:51', '', 1);
INSERT INTO `website` VALUES (127, 'jiemn', 'http://www.cecic.com.cn/', 'D:/crawler', 'unstructed', 0, 1, '2019-10-28 23:00:19', '', 0);
INSERT INTO `website` VALUES (128, 'CCEN', 'http://www.ccen.info/', 'D:/crawler', 'unstructed', 0, 0, '2019-10-29 13:04:15', '', 0);
INSERT INTO `website` VALUES (144, '浪潮扶贫', 'http://ai.inspur.com/Archive/PoorFamilyList', 'D:/crawler/inspurTest', 'structed', 2, 1, '2019-11-02 15:29:18', '', 1);

SET FOREIGN_KEY_CHECKS = 1;
