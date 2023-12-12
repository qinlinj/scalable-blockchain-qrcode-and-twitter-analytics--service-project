package edu.cmu

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{coalesce, col, collect_list, count, explode, lit, lower, max, size, struct, to_json, to_timestamp, when}


object ETLScala {

  val text = "mtvhottest\ngameinsight\niheartawards\nandroid\nandroidgames\nvideomtv2016\nrtã\u0081—ã\u0081Ÿäººå…¨å“¡ãƒ•ã‚©ãƒ\u00ADãƒ¼ã\u0081™ã‚‹\nbestfanarmy\nrt\nnow2016\nteamfollowback\nretweet\nnowplaying\nipad\nipadgames\nfollowback\nopenfollow\nØ§Ù„Ø³Ø¹ÙˆØ¯ÙŠØ©\nsougofollow\nnblnabilavoto\nlove\nç›¸äº’ãƒ•ã‚©ãƒ\u00ADãƒ¼\nØ§Ù„Ù‡Ù„Ø§Ù„\nfollow\nkca\nfollowtrick\nnews\nfollowme\nØ§Ù„Ø±ÙŠØ§Ø¶\nØ±ØªÙˆÙŠØª\niphone\nwin\njob\næ‹¡æ•£å¸Œæœ›\nØªØ·Ø¨ÙŠÙ‚_Ù‚Ø±Ø¢Ù†Ù‰\nquran\nnp\nnowonedirection\nØ§Ù„Ù†ØµØ±\nporn\nãƒˆãƒ¬ã‚¯ãƒ«\ntuitutil\nhadith\nteambts\nsex\nï·º\njobs\nØ¢Ø³Ø±Ø¹_Ø±ØªÙˆÙŠØª\nç›¸äº’å¸Œæœ›\nlovatics\nbestmusicvideo\nnbavote\nvideoveranomtv\nff\nã\u0081¾ã\u0081¨ã‚\u0081\nfree\nsoompiawards\nmgwv\nØ§Ù„Ø§ØªØ\u00ADØ§Ø¯\nfollowmejp\nmusic\nØ³ÙƒØ³\nnowfifthharmony\nteamexo\nsoundcloud\nteamsuperjunior\ngiveaway\nbeliebers\nmufc\nepicmobclothing\nlittlemonsters\nquote\nØ±ÙŠØªÙˆÙŠØª\nonedirection\namazon\nselfie\nheatonedirection\nhiring\nmpn\ntfbjp\ntrecru\niphonegames\ngain\nyoutube\nizmirescort\nvideo\nØ§Ù„ÙƒÙˆÙŠØª\nfashion\ntfb\ntravel\nbigolive\nØ¬Ø¯Ø©\nanotherfollowtrain\nteamgot7\nxxx\nksa\nÙ†Ø´Ø±_Ø³ÙŠØ±ØªÙ‡\nsaudi\nvote1duk\nhot\nsexy\nharmonizers\nãƒ¢ãƒ³ã‚¹ãƒˆ\nÙ‚Ø·Ø±\nØºØ±Ø¯_Ø¨ØµÙˆØ±Ø©\nteen\nsoma\nå‹•ç”»\nØ¯Ø¹Ø§Ø¡\nfollow2befollowed\nf4f\nart\ncountkun\nwmaonedirection\nÙ…ØµØ±\nhalamadrid\nphotography\nbelieber\nmarketing\nremajaindonesia\nwmabritneyspears\nmileyformmva\ngoldenglobes\nlfc\nØ§Ù„Ø§Ù‡Ù„ÙŠ\nÙ…ÙƒØ©\nvenezuela\ntcfollowtrain\ncge\npussy\nselenators\ntbt\nheatjustinbieber\nselenaformmva\nãƒ¤ãƒ•ã‚ªã‚¯\ncompetition\ntcot\nlibra\nvirgo\nakubutuhsentuhanlelaki\nusa\nØ§Ù„Ø¨Ø\u00ADØ±ÙŠÙ†\nbringbackourgirls\nbestcollaboration\nad\ntech\nsocialmedia\nØªØ§Ù…Ù„Ø§Øª_Ø§ÙŠÙ…Ø§Ù†ÙŠØ©\naries\nleo\ncancer\nfollowers\nbusiness\numrei\nnsfw\nØ§Ù„Ø¯Ù…Ø§Ù…\nÙ†ÙŠÙƒ\nlovelive\ntrump\nbot\nquotes\nbts\nme\nØºØ±Ø¯_Ø¨Ø§Ù„Ø®ÙŠØ±\nØ¹Ø§Ø¬Ù„\nØ§Ù„Ø´Ø¨Ø§Ø¨\nmovie\nØ³ÙˆØ±ÙŠØ§\nØ§Ù„Ø§Ù…Ø§Ø±Ø§Øª\ninspiration\ntimber\nbestcover\nØ¯Ø¨ÙŠ\nnew\nmtvsummerstar\nfxch\nãƒ‘ã‚ºãƒ‰ãƒ©\nØ´Ø¹Ø±\ngot7\nteamretweet\ngemini\nrt2gain\nvotetris\nvotethewanteduk\nfacetoface\ndragmedown\ncfc\nØ¨ÙˆØ\u00AD\nfanarmy\nsiguemeytesigo\nbahrain\nitunes\nØ§Ù\u0081Ù„Ø§Ù…_Ø³ÙƒØ³\nquotesalieakbaryks\nfatego\nakb48\ntaurus\niran\nlrt\nboobs\nè‰¦ã\u0081“ã‚Œ\netsy\nbestmoviesong\nvotekatniss\nhealth\nÙ‚Ø\u00ADØ¨Ù‡\nnothuman\nkangenkakrianaantoinette\nãƒªãƒ•ã‚©ãƒ\u00ADãƒ¼\nphoto\nautofollow\nnoticias\n1dongma\nå\u0090\u008Dè¨€\nlistenlive\nworkfromhome\npixiv\nteenchoice\nmh370\nmaga\nlol\nuae\nç¥\u009Dã\u0081£ã\u0081¦ã\u0081\u008Fã‚Œã‚‹äººrt\npisces\nhiphop\nass\n5sosfam\nbelieveinmagicball\nwmajustinbieber\nstyle\nexo\nsidetoside\nscorpio\nfollowdaibosyu\nlineãƒžãƒ³ã‚¬\nmilf\në°©íƒ„ì†Œë…„ë‹¨\naquarius\ntwitter\nkcamexico\nagqr\nØ³Ø§Ù„Ø¨\nmpoints\ncantstopthefeeling\nsyria\nÙ…ØºÙ„ÙŠÙ‡Ø§_Ø§Ù„Ø²ÙŠÙ†_ØªØ¬Ø±ÙŠØ¨ÙŠ\nlondon\nØµÙˆØ±Ø©\nhappynewyear\noomf\nnaked\nã‚¢ãƒžã‚¾ãƒ³\nnaverã\u0081¾ã\u0081¨ã‚\u0081\nseo\npjnet\npltl\nnude\nchristmas\ninstagram\nrefollow\nasmsg\nsexdate\nfood\nsobatindonesia\nauspol\njewelry\nfgo\nasyamsulzakaria\nfootball\nyespimpmysummerball\nnowjustinbieber\nØªØ·Ø¨ÙŠÙ‚_Ø§Ø°ÙƒØ§Ø±\nhappy\nØ¹Ø¬Ù„Ø§Ù†_ÙˆØ§Ø®ÙˆØ§Ù†Ù‡\nÙƒØ³\nadult\nfb\nfitness\nØ§Ù„ÙŠÙ…Ù†\nanal\nØ·ÙŠØ²\nsheskindahotmusicvideo\nnba\ntweetkepo\n2ch\nåŠ£åŒ–ã‚³ãƒ”ãƒ¼\nuk\ntwimaker\ntits\nsnapchat\nleadership\ncareerarc\ncapricorn\nØ±ÙˆØ§Ø¨Ø·_Ø³ÙƒØ³\nbeauty\nfact\nÙ„Ø²ÙŠØ§Ø¯Ø©_Ø¹Ø¯Ø¯_Ù…ØªØ§Ø¨Ø¹ÙŠÙ†\nã‚\u008Fãƒ¼ãƒ¼ãƒ¼ãƒ¼ã‚¸ãƒ£ãƒ‹ã‚ªã‚¿ã\u0081•ã‚“ã\u0081¨ç¹‹ã\u0081Œã‚‹ã\u0081Šæ™‚é–“ã\u0081Œã\u0081¾ã\u0081„ã‚Šã\u0081¾ã\u0081—ã\u0081Ÿã\u0081„ã\u0081£ã\u0081±ã\u0081„ç¹‹ã\u0081Œã‚Šã\u0081¾ã\u0081—ã‚‡\nlt\nØªØ·Ø¨ÙŠÙ‚_ØªØ§Ù…Ù„Ø§Øª_Ø§ÙŠÙ…Ø§Ù†ÙŠØ©\nwcw\npillowtalk\nnyc\n4musiclfsdirectioners\nrepost\namateur\nfacebook\nperiscope\nfav\nstartup\nraw\nprayforsouthkorea\ndesign\nØ±ØªÙˆÙŠØª_Ø¨Ù„Ø¢_ØªÙˆÙ‚Ù\u0081\nsagittarius\nehemehem\nmaunyaapaan\nverifydjzoodel\nnature\nescortizmir\ntakutadaapaapa\nåŠ è—¤ç”±ç¾Žå\u00AD\u0090\nerotic\nnashsnewvideo\nkindle\nØ¯Ø±Ø±\ndeals\nf1\nnowladygaga\nspinnrtaylorswift\nwmaexo\nç„¡æ–™\nØ§Ø³ØªØºÙ\u0081Ø§Ø±\nãƒ¢ãƒ³ã‚¹ãƒˆã‚„ã‚‹ã\u0081ªã‚ˆ\nå…±æ„Ÿã\u0081—ã\u0081Ÿã‚‰rt\nshoes\nsmap\ncetiga\nchibicybercommunity\nbitcoin\nnfl\nperfectday\nempleo\nokuga\nØ§Ù„Ø¹Ø±Ø§Ù‚\nØ±ØªÙˆÙŠØª_Ù„Ø²ÙŠØ§Ø¯Ø©\nmeetthevamily\nukraine\nwedding\nØ²Ø¨\nØ§Ù„Ø¥Ù…Ø§Ø±Ø§Øª\nmplusrewards\nislam\nØ§Ø¶ØºØ·_Ù\u0081ÙˆÙ„Ùˆ\nlife\nÙ…Ù‚Ø§Ø·Ø¹_Ø³ÙƒØ³\ncuba\ntweetbatt\nrussia\nmobile\nlive\nromance\nç›¸äº’\nvintage\nobamafarewell\nØ§Ù„Ø£Ù‡Ù„ÙŠ\nrespect\nã‚³ãƒ©ãƒœã‚\u00ADãƒ£ã‚¹\nfollowngain\napple\nØ§Ù„Ù‚ØµÙŠÙ…\nretweets\nbreaking\nteamautofollow\nteenwolf\nporno\nyesallwomen\nspring\nebook\nmoney\nØ§Ù\u0081Ù„Ø§Ù…\nÙ…Ø\u00ADØ§Ø±Ù…\nØ²ÙŠØ§Ø¯Ø©_Ù…ØªØ§Ø¨Ø¹ÙŠÙ†\ninstantfollow\nØ³ÙƒØ³_ÙˆØ±Ø¹Ø§Ù†\nã‚¨ãƒ\u00AD\nsummer\nexsandohs\ntakipedenitakipederim\nnhk\nãƒ©ãƒ–ãƒ©ã‚¤ãƒ\u0090ãƒ¼ã\u0081¯rt\nsougo\nå£°å„ªç·\u008Fé\u0081¸æŒ™\nentrepreneur\nrtã\u0081—ã\u0081Ÿäººã\u0081«ã‚„ã‚‹\npll\nØ¯Ø§Ø¹Ø´\nc91\nÙ†Ø¬Ø±Ø§Ù†\nobama\nchasingcameron\nã‚¢ãƒ€ãƒ«ãƒˆ\n2ne1\nmadrid\nsherlock\ncute\nè\u0090Œã\u0081ˆã‚‹ã‚·ãƒ\u0081ãƒ¥ã‚¨ãƒ¼ã‚·ãƒ§ãƒ³ã\u0081«å\u0090Œæ„\u008Fã\u0081ªã‚‰rt\nbest\nØ§Ù„Ø³Ø¹ÙˆØ¯ÙŠÙ‡\ntruth\nynwa\nindia\nblog\nØ§ÙƒØ´Ù†_ÙŠØ§_Ø¯ÙˆØ±ÙŠ\ngossip\nbooks\nblessed\nps4share\nfreebiefriday\nbeautiful\nmustfollow\n500aday\nmplusplaces\nã‚«ã‚²ãƒ—ãƒ\u00ADå¥½ã\u0081\u008Dã\u0081ªäººrt\nfollowall\nhandmade\nafc\næ‹¡æ•£\nmendesarmy\narsenal\nçµ\u0090å©š\nãƒ©ãƒ–ãƒ©ã‚¤ãƒ–\notraindianapolis\ndubai\npics\nadmindirectpopularpenipu\nfunny\npakistan\nfamily\nomspiktanya\nteamfollow\n4musiclfsbeliebers\nØµØ¨Ø§Ø\u00AD_Ø§Ù„Ø®ÙŠØ±\nparis\nwomen\næ\u0081‹æ„›\nfollow4follow\ndownload\nuber\nä¹ƒæœ¨å\u009D‚46\nØ\u00ADÙ‚ÙŠÙ‚Ø©\nyahooãƒ‹ãƒ¥ãƒ¼ã‚¹\nforex\nfriends\nã\u0081³ã‚ˆãƒ¼ã‚“\nØ³Ø§Ù…ÙŠ_Ø§Ù„Ø¬Ø§Ø¨Ø±\nãƒ\u008Fã‚¤ã‚\u00ADãƒ¥ãƒ¼ã‚¯ãƒ©ã‚¹ã‚¿ã\u0081•ã‚“ã\u0081¨ç¹‹ã\u0081Œã‚Šã\u0081Ÿã\u0081„\netsymnt\ncsrclassics\nã‚¢ãƒ¡ãƒ–ãƒ\u00AD\nshopping\nshestheone\nrip\nbigbang\ntheresistance\nÙˆØ±Ø¹Ø§Ù†\na\nradio\nÙ†Ø¬ÙˆÙ…_Ø§Ù„Ø±ØªÙˆÙŠØª\nufc190\nÙ\u0081ÙˆÙ„ÙˆØ¨Ø§Ùƒ\narchitecture\nsale\ngameofthrones\ncontest\ncsrracing\nØ\u00ADÙ‚ÙŠÙ‚Ù‡\niartg\nrtã\u0081—ã\u0081Ÿäººã\u0081§æ°—ã\u0081«ã\u0081ªã\u0081£ã\u0081Ÿäººãƒ•ã‚©ãƒ\u00ADãƒ¼ã\u0081™ã‚‹\nwork\nbabes\nä»Šã\u0081®å°\u008Få\u00AD¦ç”Ÿã\u0081¯çŸ¥ã‚‰ã\u0081ªã\u0081„\ndating\neducation\nwwe\ngoogle\nsports\nkasabi\ntechnology\nsuccess\ndemilovato\nnhl15bergeron\nfun\nçµµæ\u008F\u008Fã\u0081\u008Dã\u0081•ã‚“ã\u0081¨ç¹‹ã\u0081Œã‚Šã\u0081Ÿã\u0081„\nmcfc\nØ¨ÙŠØ¹_Ù…ØªØ§Ø¨Ø¹ÙŠÙ†\nØ§Ù„Ø§Ø\u00ADØ³Ø§Ø¡\nstarwars\niot\nnblalinavoto\ncaracas\nselenagomez\næ ¼è¨€\nmotivation\ngod\ntrabajo\nrealmadrid\nnigeria\nsales\nunfalert\nnasigudegmeruya\nØ¹Ù…Ø§Ù†\nÙ„Ø²ÙŠØ§Ø¯Ø©\ngirls\nØ§Ù†ØªØ®Ø¨ÙˆØ§_Ø§Ù„Ø¹Ø±Øµ\nnhkç´…ç™½\nhome\ndigital\nãƒ•ã‚©ãƒ\u00ADãƒ¼è¿”ã\u0081—\ngantenggantengserigalasctv\nå\u008F¯æ„›ã\u0081„ã\u0081¨æ€\u009Dã\u0081£ã\u0081Ÿã‚‰rt\negypt\njapan\ni\nãƒ‹ãƒ¥ãƒ¼ã‚¹\nisrael\nbestlyrics\nnct127\nshoutout\ncool\nchina\nconcours\nniggernavy\nfollowpyramid\ngay\nkesombonganalieakbar\nwishbdaybywelfare\nolsenwpmoychallenge\nbeach\nindonesia\ndjkingassassin\nfcblive\nnw\nåœ°éœ‡\nentertainment\nces2017\ninteriordesign\nrealestate\nrakutenichiba\nsmurfsvillage\nweather\nã‚¨ãƒ\u00ADå‹•ç”»\nè‰¦ã\u0081“ã‚Œç‰ˆæ·±å¤œã\u0081®çœŸå‰£ã\u0081Šçµµæ\u008F\u008Fã\u0081\u008D60åˆ†ä¸€æœ¬å‹\u009Dè²\nç›¸äº’é™\u0090å®š\nãƒ©ãƒ–ãƒ©ã‚¤ãƒ\u0090ãƒ¼ã\u0081¨ç¹‹ã\u0081Œã‚Šã\u0081Ÿã\u0081„\nchicago\ngirl\nØªØ³Ø¯ÙŠØ¯_Ù‚Ø±ÙˆØ¶\nrbooks\nØ§Ø°ÙƒØ§Ø±\nØºØ±Ø¯_Ø¨Ø°ÙƒØ±_Ø§Ù„Ù„Ù‡\nmlb\nlesanges6\np2\nbook\npojoksatu\nÙ…ØªØµØ¯Ø±_Ù„Ø§ØªÙƒÙ„Ù…Ù†ÙŠ\ntoronto\napp\nmexico\nãƒ‰ãƒªãƒ•ãƒˆã‚¹ãƒ”ãƒªãƒƒãƒ„\nØ¨Ø±Ø´Ù„ÙˆÙ†Ø©\njesus\nrtã\u0081—ã\u0081Ÿã\u0081‚ã‚‰ã\u0081—ã\u0081£ã\u0081\u008Få…¨å“¡ãƒ•ã‚©ãƒ\u00ADãƒ¼ã\u0081™ã‚‹\namwriting\nØ±ØªÙˆÙŠØª_Ù‚ÙˆÙŠ_ÙˆØ³Ø±ÙŠØ¹\nbokep\ntokyomx\nkcacolombia\nebay\nå±…é…’å±‹\nØ§Ù„Ù…Ø¯ÙŠÙ†Ø©\nedm\nshare\nsmpn12yksuksesun\nÙ…ÙƒÙˆÙ‡\nØ³ÙƒØ³_Ø¹Ø±Ø¨ÙŠ\ncat\nufc207\nmcm\nhindkanapakkojawab\nÙ…ØªØ¶Ø±Ø±ÙŠ_Ù…Ø§Ù†_Ø¯ÙŠÙ\u0081Ø§Ù†\nfilmtania\nçŒ«\nus\nØ¬Ù†Ø³_Ø³ÙƒØ³_Ù†ÙŠÙƒ_Ø§ØºØªØµØ§Ø¨_Ø·ÙŠØ²_Ø³Ø§Ø®Ù†_sex_Ù‡ÙŠÙ\u0081Ø§Ø¡_ÙˆÙ‡Ø¨ÙŠ_Ù„Ù„ÙƒØ¨Ø§Ø±_Ù\u0081Ù‚Ø·\nÙ\u0081ÙˆÙ„ÙˆÙ…ÙŠ\nchoicetwit\nlyft\nå‡ºä¼šã\u0081„\ncelebrities\nc91ã‚³ã‚¹ãƒ—ãƒ¬\nØ\u00ADØ³Ø§Ø¨_ÙŠØ³ØªØ\u00ADÙ‚_Ø§Ù„Ù…ØªØ§Ø¨Ø¹Ù‡\nmÃ©xico\nØ\u00ADØ§Ø¦Ù„\ncomebackhome\ngoharshahi\nØªØ³Ø¨ÙŠØ\u00AD\nshawnfollowme\ndog\nØ¬Ø§Ø²Ø§Ù†\nfavcounter\nsexting\ninstantfollowback\nbairavaa\ncats\nthrowback\nmomoclo\nØ¨Ø±_Ø§Ù„ÙˆØ§Ù„Ø¯ÙŠÙ†\nnbljosephinevoto\nãƒ¢ãƒ¼ãƒ‹ãƒ³ã‚°å¨˜\nmakeup\nnblaleydavoto\ngh2015\nÙ…Ù…Ø\u00ADÙˆÙ†Ù‡\nbigdata\nhere\ntityfollowtrain\naaa\npiscis\nfollowbackseguro\n2a\ntrndnl\ngamedev\nonline\nisis\ngÃ¼nkÃ¶mÃ¼rkarasÄ±\ncanada\nØ§Ø¨Ù‡Ø§\nwebeliveinyoukris\nturkey\ngoodmorning\nalbert_stanlie\nbiafra\nÙ…ØªØ§Ø¨Ø¹ÙŠÙ†Ùƒ\ninfo\nrtã\u0081—ã\u0081Ÿäººãƒ•ã‚©ãƒ\u00ADãƒ¼ã\u0081™ã‚‹\nswifties\nã‚²ãƒ¼ãƒ\nthf\ntlrp\nbarcelona\nmothersday\nnewyork\nØ¬Ù†Ø³\nsalud\nfrance\narfahmdtampan\nØ¯ÙŠÙˆØ«\nniconews\nÙ\u0081Ø±ØµÙ€Ù€ØªÙƒ\nÙ‚Ø±ÙˆØ¨_Ø§Ù„Ø³Ø¹ÙˆØ¯ÙŠØ©\nmovies\ntauro\nsemihvaroitayfaunfsuzkazandirtiyor\nã‚\u008Fãƒ¼ãƒ¼ãƒ¼ãƒ¼ãƒ¼ãƒ¼ã‚¸ãƒ£ãƒ‹ã‚ªã‚¿ã\u0081•ã‚“ã\u0081¨ç¹‹ã\u0081Œã‚‹ã\u0081Šæ™‚é–“ã\u0081Œã\u0081¾ã\u0081„ã‚Šã\u0081¾ã\u0081—ã\u0081Ÿã\u0081ªã\u0081®ã\u0081§ã\u0081„ã\u0081£ã\u0081±ã\u0081„ç¹‹ã\u0081Œã‚Šã\u0081¾ã\u0081—ã‚‡ã\u0081\u009Dã\u0081—ã\u0081¦æ¿ƒã\u0081\u008Fçµ¡ã‚“ã\u0081§å…ƒæ°—ã\u0081ªã\u0081£ã\u0081¡ã‚ƒã\u0081„ã\u0081¾ã\u0081—ã‚‡rtã\u0081—ã\u0081¦ã\u0081\u008Fã‚Œã\u0081Ÿæ–¹ã\u0081§æ°—ã\u0081«ã\u0081ªã\u0081£ã\u0081Ÿæ–¹ã\u0081Šè¿Žã\u0081ˆã\u0081§ã\u0081™\nê°“ì„¸ë¸\u0090\nÙ\u0081Ø\u00ADÙ„\ngÃ©minis\ntntweeters\nworld\nvalencia\nå€Ÿé‡‘ã\u0081‚ã‚‹ã\u0081‹ã‚‰ã‚®ãƒ£ãƒ³ãƒ–ãƒ«ã\u0081—ã\u0081¦ã\u0081\u008Fã‚‹\nhair\nØ§Ù„Ù\u0081ØªØ\u00AD\nØ§Ù„Ù…Ù„ÙƒÙŠ\nÙ\u0081ÙŠØ¯ÙŠÙˆ\nmlkday\nbizitalk\nØ§Ù„Ø³ÙŠØ³ÙŠ\nstocks\ncÃ¡ncer\nsmile\nbreakingnews\nhealthcare\nãƒ•ã‚©ãƒ\u00ADãƒ¼\ngiants\ntbs\nthrowbackthursday\nescreveai\nØ°ÙƒØ±\ngift\nuclfinal\nqatar\nasian\ncapricornio\nØ¹Ø¬Ù„Ø§Ù†\nheat5sos\nØ³Ù…Ù‡\nåµ\u0090"
  val list = text.toLowerCase().split("\n")

  /**
   * Input graph is a plain text file of the jsons:
   *
   * After filtering, data will be written to mysql database
   *
   * @param inputPath path of the input graph.
   * @param spark          the SparkSession.
   */
  def dataETL(
                         inputPath: String,
                         outputPath: String,
                         spark: SparkSession): Unit = {
    val sc = spark.sparkContext

    // read jsons from file
    val raw_df=spark.read.option("mode","DROPMALFORMED").option("charset", "UTF-8").json(inputPath).distinct()

    // filter to get valid tweet object
    val filtered= raw_df
        .withColumn("id", coalesce(col("id"), col("id_str").cast("long")))
        .where(col("id").isNotNull)
        .withColumn("user.id", coalesce(col("user.id"), col("user.id_str").cast("long")))
        .where(col("user.id").isNotNull)
        .where(col("created_at").isNotNull)
        .where(col("text").isNotNull.and(col("text").notEqual("")))
        .where(col("lang").isin(List("ar", "en", "fr", "in", "pt", "es", "tr", "ja"): _*))
        .where(col("entities.hashtags").isNotNull.and(size(col("entities.hashtags")) > 0))
        .dropDuplicates("id")
        .withColumn("in_reply_to_user_id", coalesce(col("in_reply_to_user_id"), col("in_reply_to_user_id_str").cast("long")))
        .withColumn("retweeted_status.user.id", coalesce(col("retweeted_status.user.id"), col("retweeted_status.user.id_str").cast("long")))
        .withColumn("created_at", to_timestamp(col("created_at"), "EEE MMM dd HH:mm:ss Z yyyy"))
        .withColumn("entities_hashtags", to_json(col("entities.hashtags.text")))
        .cache()

    val hashtagStruct = struct(col("hashtag").alias("hashtag"), col("count").alias("count"))

    val user_hashtag=filtered
      .select(col("user.id").as("id"),
        col("entities.hashtags.text").as("hashtags"))
      .withColumn("hashtag", explode(col("hashtags")))
      .withColumn("hashtag",lower(col("hashtag")))
      .groupBy("id","hashtag")
      .agg(count("*").alias("count"))
      .where(!col("hashtag").isin(list:_*))
      .groupBy("id")
      .agg(collect_list(hashtagStruct).as("hashtag_counts"))
      .cache()

    // create user table
    // first find all users
    val all_users=filtered
      .select(col("user"),col("created_at"),col("id").alias("tweet_id"))
      .union(filtered
        .select(
          col("retweeted_status.user"),
          col("created_at"),
          col("id").alias("tweet_id")))
      .where(col("user.id").isNotNull)
      .where(col("user.id").notEqual(0))
      .distinct()

    // find the latest info of a user based on outter twitter's created_at
    val maxDF=all_users
      .groupBy("user.id")
      .agg(max("created_at").alias("max_created_at")
        ,max("tweet_id").alias("tweet_id"))

    val users = all_users
      .join(maxDF,(all_users("user.id")===maxDF("id"))
        &&(all_users("created_at")===maxDF("max_created_at"))
        &&(all_users("tweet_id")===maxDF("tweet_id")),
        "right")
      .where(col("user.id").isNotNull)
      .where(col("user.id").notEqual(0))
      .join(user_hashtag,all_users("user.id")===user_hashtag("id"),"Outer")
      .select(
        col("user.id").alias("id"),
        col("user.screen_name").alias("screen_name"),
        col("user.description").alias("description"),
        to_json(col("hashtag_counts")).alias("hashtag_counts"),
        col("created_at"))
      .dropDuplicates("id")
      .na.fill("",Array("screen_name","description","hashtag_counts"))
      .cache()


//
    val renamed = filtered
      .select(
        col("id").alias("tweet_id"),
        col("user.id").alias("user_id"),
        col("text").alias("text"),
        col("entities_hashtags").alias("entities_hashtags"),
        col("created_at").cast("String").alias("tweet_created_at"),
        col("in_reply_to_user_id").alias("reply_id"),
        col("retweeted_status.user.id").alias("retweet_id"))

    val joinedTweets = renamed.as("r")
      .join(users.alias("senders"), col("r.user_id") === col("senders.id"), "left")
      .join(users.alias("contacts"), (col("r.reply_id") === col("contacts.id")
        || col("r.retweet_id") === col("contacts.id")), "left")
      .select(
        renamed("tweet_id"),
        renamed("user_id"),
        renamed("text"),
        renamed("entities_hashtags"),
        renamed("tweet_created_at"),
        renamed("reply_id"),
        renamed("retweet_id"),
        col("senders.screen_name").alias("sender_screen_name"),
        col("senders.description").alias("sender_description"),
        col("senders.hashtag_counts").alias("sender_hashtag_counts"),
        col("contacts.screen_name").alias("contact_screen_name"),
        col("contacts.description").alias("contact_description"),
        col("contacts.hashtag_counts").alias("contact_hashtag_counts")
      )

    joinedTweets
      .write
      .option("delimiter", "\t")
      .option("quoteAll", "true")
      .csv(outputPath + "/CompleteTweets")


//
//    val senderJoin= renamed
//      .join(users, renamed("user_id") === users("id"), "left")
//      .select(
//        col("tweet_id"),
//        col("user_id"),
//        col("text"),
//        col("entities_hashtags"),
//        col("tweet_created_at"),
//        col("reply_id"),
//        col("retweet_id"),
//        col("screen_name").alias("sender_screen_name"),
//        col("description").alias("sender_description"),
//        col("hashtag_counts").alias("sender_hashtag_counts")
//      )
//
//    val contactJoin=senderJoin
//      .join(users, (senderJoin("reply_id")===users("id")
//        ||senderJoin("retweet_id")===users("id")),"left")
//      .select(
//        col("tweet_id"),
//        col("user_id"),
//        col("text"),
//        col("entities_hashtags"),
//        col("tweet_created_at"),
//        col("reply_id"),
//        col("retweet_id"),
//        col("sender_screen_name"),
//        col("sender_description"),
//        col("sender_hashtag_counts"),
//        col("screen_name").alias("contact_screen_name"),
//        col("description").alias("contact_description"),
//        col("hashtag_counts").alias("contact_hashtag_counts")
//      )
//      .withColumn("type",when(col("reply_id").isNotNull, lit("reply"))
//        .when(col("retweet_id").isNotNull, lit("retweet"))
//        .otherwise(null)
//      )
//
//    contactJoin
//      .write
//      .option("delimiter", "\t")
//      .option("quoteAll", "true")
//      .option("charset", "UTF-8")
//      .csv(outputPath+"/CompleteTweets")

    spark.stop()
  }

  /**
   * @param args it should be called with two arguments, the input path, and the output path.
   */
  def main(args: Array[String]): Unit = {
    val aws_accesskey=System.getenv("aws_accesskey")
    val aws_secretkey=System.getenv("aws_secretkey")
    val outputPath=System.getenv("s3BucketName")
    val conf = new SparkConf().setAppName("TwitterETL")
    val spark =SparkSession.builder.config(conf)
      .config("spark.hadoop.fs.s3a.access.key", aws_accesskey)
      .config("spark.hadoop.fs.s3a.secret.key", aws_secretkey)
      .getOrCreate()

    val inputGraph = args(0)

    dataETL(inputGraph,outputPath,spark)

  }
}
