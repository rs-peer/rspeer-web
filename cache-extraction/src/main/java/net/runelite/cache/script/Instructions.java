/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2018-2019, Hunter WB <hunterwb.com>
 * Copyright (c) 2019, Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache.script;

import java.util.HashMap;
import java.util.Map;

import static net.runelite.cache.script.Opcodes.*;

public class Instructions
{
	private final Map<Integer, Instruction> instructions = new HashMap<>();
	private final Map<String, Instruction> instructionsByName = new HashMap<>();

	public void init()
	{
		add(ICONST, "iconst");
		add(GET_VARP, "get_varp");
		add(SET_VARP, "set_varp");
		add(SCONST, "sconst");
		add(JUMP, "jump");
		add(IF_ICMPNE, "if_icmpne");
		add(IF_ICMPEQ, "if_icmpeq");
		add(IF_ICMPLT, "if_icmplt");
		add(IF_ICMPGT, "if_icmpgt");
		add(RETURN, "return");
		add(GET_VARBIT, "get_varbit");
		add(SET_VARBIT, "set_varbit");
		add(IF_ICMPLE, "if_icmple");
		add(IF_ICMPGE, "if_icmpge");
		add(ILOAD, "iload");
		add(ISTORE, "istore");
		add(SLOAD, "sload");
		add(SSTORE, "sstore");
		add(JOIN_STRING, "join_string");
		add(POP_INT, "pop_int");
		add(POP_STRING, "pop_string");
		add(INVOKE, "invoke");
		add(GET_VARC_INT, "get_varc_int");
		add(SET_VARC_INT, "set_varc_int");
		add(DEFINE_ARRAY, "define_array");
		add(GET_ARRAY_INT, "get_array_int");
		add(SET_ARRAY_INT, "set_array_int");
		add(GET_VARC_STRING_OLD, "get_varc_string_old");
		add(SET_VARC_STRING_OLD, "set_varc_string_old");
		add(GET_VARC_STRING, "get_varc_string");
		add(SET_VARC_STRING, "set_varc_string");
		add(SWITCH, "switch");
		add(CC_CREATE, "cc_create");
		add(CC_DELETE, "cc_delete");
		add(CC_DELETEALL, "cc_deleteall");
		add(CC_FIND, "cc_find");
		add(IF_FIND, "if_find");
		add(CC_SETPOSITION, "cc_setposition");
		add(CC_SETSIZE, "cc_setsize");
		add(CC_SETHIDE, "cc_sethide");
		add(CC_SETNOCLICKTHROUGH, "cc_setnoclickthrough");
		add(CC_SETSCROLLPOS, "cc_setscrollpos");
		add(CC_SETCOLOUR, "cc_setcolour");
		add(CC_SETFILL, "cc_setfill");
		add(CC_SETTRANS, "cc_settrans");
		add(CC_SETLINEWID, "cc_setlinewid");
		add(CC_SETGRAPHIC, "cc_setgraphic");
		add(CC_SET2DANGLE, "cc_set2dangle");
		add(CC_SETTILING, "cc_settiling");
		add(CC_SETMODEL, "cc_setmodel");
		add(CC_SETMODELANGLE, "cc_setmodelangle");
		add(CC_SETMODELANIM, "cc_setmodelanim");
		add(CC_SETMODELORTHOG, "cc_setmodelorthog");
		add(CC_SETTEXT, "cc_settext");
		add(CC_SETTEXTFONT, "cc_settextfont");
		add(CC_SETTEXTALIGN, "cc_settextalign");
		add(CC_SETTEXTSHADOW, "cc_settextshadow");
		add(CC_SETOUTLINE, "cc_setoutline");
		add(CC_SETGRAPHICSHADOW, "cc_setgraphicshadow");
		add(CC_SETVFLIP, "cc_setvflip");
		add(CC_SETHFLIP, "cc_sethflip");
		add(CC_SETSCROLLSIZE, "cc_setscrollsize");
		add(CC_RESUME_PAUSEBUTTON, "cc_resume_pausebutton");
		add(CC_SETFILLCOLOUR, "cc_setfillcolour");
		add(CC_SETLINEDIRECTION, "cc_setlinedirection");
		add(CC_SETOBJECT, "cc_setobject");
		add(CC_SETNPCHEAD, "cc_setnpchead");
		add(CC_SETPLAYERHEAD_SELF, "cc_setplayerhead_self");
		add(CC_SETOBJECT_NONUM, "cc_setobject_nonum");
		add(CC_SETOBJECT_ALWAYS_NUM, "cc_setobject_always_num");
		add(CC_SETOP, "cc_setop");
		add(CC_SETDRAGGABLE, "cc_setdraggable");
		add(CC_SETDRAGGABLEBEHAVIOR, "cc_setdraggablebehavior");
		add(CC_SETDRAGDEADZONE, "cc_setdragdeadzone");
		add(CC_SETDRAGDEADTIME, "cc_setdragdeadtime");
		add(CC_SETOPBASE, "cc_setopbase");
		add(CC_SETTARGETVERB, "cc_settargetverb");
		add(CC_CLEAROPS, "cc_clearops");
		add(CC_SETONCLICK, "cc_setonclick");
		add(CC_SETONHOLD, "cc_setonhold");
		add(CC_SETONRELEASE, "cc_setonrelease");
		add(CC_SETONMOUSEOVER, "cc_setonmouseover");
		add(CC_SETONMOUSELEAVE, "cc_setonmouseleave");
		add(CC_SETONDRAG, "cc_setondrag");
		add(CC_SETONTARGETLEAVE, "cc_setontargetleave");
		add(CC_SETONVARTRANSMIT, "cc_setonvartransmit");
		add(CC_SETONTIMER, "cc_setontimer");
		add(CC_SETONOP, "cc_setonop");
		add(CC_SETONDRAGCOMPLETE, "cc_setondragcomplete");
		add(CC_SETONCLICKREPEAT, "cc_setonclickrepeat");
		add(CC_SETONMOUSEREPEAT, "cc_setonmouserepeat");
		add(CC_SETONINVTRANSMIT, "cc_setoninvtransmit");
		add(CC_SETONSTATTRANSMIT, "cc_setonstattransmit");
		add(CC_SETONTARGETENTER, "cc_setontargetenter");
		add(CC_SETONSCROLLWHEEL, "cc_setonscrollwheel");
		add(CC_SETONCHATTRANSMIT, "cc_setonchattransmit");
		add(CC_SETONKEY, "cc_setonkey");
		add(CC_SETONFRIENDTRANSMIT, "cc_setonfriendtransmit");
		add(CC_SETONCLANTRANSMIT, "cc_setonclantransmit");
		add(CC_SETONMISCTRANSMIT, "cc_setonmisctransmit");
		add(CC_SETONDIALOGABORT, "cc_setondialogabort");
		add(CC_SETONSUBCHANGE, "cc_setonsubchange");
		add(CC_SETONSTOCKTRANSMIT, "cc_setonstocktransmit");
		add(CC_SETONRESIZE, "cc_setonresize");
		add(CC_GETX, "cc_getx");
		add(CC_GETY, "cc_gety");
		add(CC_GETWIDTH, "cc_getwidth");
		add(CC_GETHEIGHT, "cc_getheight");
		add(CC_GETHIDE, "cc_gethide");
		add(CC_GETLAYER, "cc_getlayer");
		add(CC_GETSCROLLX, "cc_getscrollx");
		add(CC_GETSCROLLY, "cc_getscrolly");
		add(CC_GETTEXT, "cc_gettext");
		add(CC_GETSCROLLWIDTH, "cc_getscrollwidth");
		add(CC_GETSCROLLHEIGHT, "cc_getscrollheight");
		add(CC_GETMODELZOOM, "cc_getmodelzoom");
		add(CC_GETMODELANGLE_X, "cc_getmodelangle_x");
		add(CC_GETMODELANGLE_Z, "cc_getmodelangle_z");
		add(CC_GETMODELANGLE_Y, "cc_getmodelangle_y");
		add(CC_GETTRANS, "cc_gettrans");
		add(CC_GETCOLOUR, "cc_getcolour");
		add(CC_GETFILLCOLOUR, "cc_getfillcolour");
		add(CC_GETINVOBJECT, "cc_getinvobject");
		add(CC_GETINVCOUNT, "cc_getinvcount");
		add(CC_GETID, "cc_getid");
		add(CC_GETTARGETMASK, "cc_gettargetmask");
		add(CC_GETOP, "cc_getop");
		add(CC_GETOPBASE, "cc_getopbase");
		add(CC_CALLONRESIZE, "cc_callonresize");
		add(IF_SETPOSITION, "if_setposition");
		add(IF_SETSIZE, "if_setsize");
		add(IF_SETHIDE, "if_sethide");
		add(IF_SETNOCLICKTHROUGH, "if_setnoclickthrough");
		add(IF_SETSCROLLPOS, "if_setscrollpos");
		add(IF_SETCOLOUR, "if_setcolour");
		add(IF_SETFILL, "if_setfill");
		add(IF_SETTRANS, "if_settrans");
		add(IF_SETLINEWID, "if_setlinewid");
		add(IF_SETGRAPHIC, "if_setgraphic");
		add(IF_SET2DANGLE, "if_set2dangle");
		add(IF_SETTILING, "if_settiling");
		add(IF_SETMODEL, "if_setmodel");
		add(IF_SETMODELANGLE, "if_setmodelangle");
		add(IF_SETMODELANIM, "if_setmodelanim");
		add(IF_SETMODELORTHOG, "if_setmodelorthog");
		add(IF_SETTEXT, "if_settext");
		add(IF_SETTEXTFONT, "if_settextfont");
		add(IF_SETTEXTALIGN, "if_settextalign");
		add(IF_SETTEXTSHADOW, "if_settextshadow");
		add(IF_SETOUTLINE, "if_setoutline");
		add(IF_SETGRAPHICSHADOW, "if_setgraphicshadow");
		add(IF_SETVFLIP, "if_setvflip");
		add(IF_SETHFLIP, "if_sethflip");
		add(IF_SETSCROLLSIZE, "if_setscrollsize");
		add(IF_RESUME_PAUSEBUTTON, "if_resume_pausebutton");
		add(IF_SETFILLCOLOUR, "if_setfillcolour");
		add(IF_SETLINEDIRECTION, "if_setlinedirection");
		add(IF_SETOBJECT, "if_setobject");
		add(IF_SETNPCHEAD, "if_setnpchead");
		add(IF_SETPLAYERHEAD_SELF, "if_setplayerhead_self");
		add(IF_SETOBJECT_NONUM, "if_setobject_nonum");
		add(IF_SETOBJECT_ALWAYS_NUM, "if_setobject_always_num");
		add(IF_SETOP, "if_setop");
		add(IF_SETDRAGGABLE, "if_setdraggable");
		add(IF_SETDRAGGABLEBEHAVIOR, "if_setdraggablebehavior");
		add(IF_SETDRAGDEADZONE, "if_setdragdeadzone");
		add(IF_SETDRAGDEADTIME, "if_setdragdeadtime");
		add(IF_SETOPBASE, "if_setopbase");
		add(IF_SETTARGETVERB, "if_settargetverb");
		add(IF_CLEAROPS, "if_clearops");
		add(IF_SETOPKEY, "if_setopkey");
		add(IF_SETOPTKEY, "if_setoptkey");
		add(IF_SETOPKEYRATE, "if_setopkeyrate");
		add(IF_SETOPTKEYRATE, "if_setoptkeyrate");
		add(IF_SETOPKEYIGNOREHELD, "if_setopkeyignoreheld");
		add(IF_SETOPTKEYIGNOREHELD, "if_setoptkeyignoreheld");
		add(IF_SETONCLICK, "if_setonclick");
		add(IF_SETONHOLD, "if_setonhold");
		add(IF_SETONRELEASE, "if_setonrelease");
		add(IF_SETONMOUSEOVER, "if_setonmouseover");
		add(IF_SETONMOUSELEAVE, "if_setonmouseleave");
		add(IF_SETONDRAG, "if_setondrag");
		add(IF_SETONTARGETLEAVE, "if_setontargetleave");
		add(IF_SETONVARTRANSMIT, "if_setonvartransmit");
		add(IF_SETONTIMER, "if_setontimer");
		add(IF_SETONOP, "if_setonop");
		add(IF_SETONDRAGCOMPLETE, "if_setondragcomplete");
		add(IF_SETONCLICKREPEAT, "if_setonclickrepeat");
		add(IF_SETONMOUSEREPEAT, "if_setonmouserepeat");
		add(IF_SETONINVTRANSMIT, "if_setoninvtransmit");
		add(IF_SETONSTATTRANSMIT, "if_setonstattransmit");
		add(IF_SETONTARGETENTER, "if_setontargetenter");
		add(IF_SETONSCROLLWHEEL, "if_setonscrollwheel");
		add(IF_SETONCHATTRANSMIT, "if_setonchattransmit");
		add(IF_SETONKEY, "if_setonkey");
		add(IF_SETONFRIENDTRANSMIT, "if_setonfriendtransmit");
		add(IF_SETONCLANTRANSMIT, "if_setonclantransmit");
		add(IF_SETONMISCTRANSMIT, "if_setonmisctransmit");
		add(IF_SETONDIALOGABORT, "if_setondialogabort");
		add(IF_SETONSUBCHANGE, "if_setonsubchange");
		add(IF_SETONSTOCKTRANSMIT, "if_setonstocktransmit");
		add(IF_SETONRESIZE, "if_setonresize");
		add(IF_GETX, "if_getx");
		add(IF_GETY, "if_gety");
		add(IF_GETWIDTH, "if_getwidth");
		add(IF_GETHEIGHT, "if_getheight");
		add(IF_GETHIDE, "if_gethide");
		add(IF_GETLAYER, "if_getlayer");
		add(IF_GETSCROLLX, "if_getscrollx");
		add(IF_GETSCROLLY, "if_getscrolly");
		add(IF_GETTEXT, "if_gettext");
		add(IF_GETSCROLLWIDTH, "if_getscrollwidth");
		add(IF_GETSCROLLHEIGHT, "if_getscrollheight");
		add(IF_GETMODELZOOM, "if_getmodelzoom");
		add(IF_GETMODELANGLE_X, "if_getmodelangle_x");
		add(IF_GETMODELANGLE_Z, "if_getmodelangle_z");
		add(IF_GETMODELANGLE_Y, "if_getmodelangle_y");
		add(IF_GETTRANS, "if_gettrans");
		add(IF_GETCOLOUR, "if_getcolour");
		add(IF_GETFILLCOLOUR, "if_getfillcolour");
		add(IF_GETINVOBJECT, "if_getinvobject");
		add(IF_GETINVCOUNT, "if_getinvcount");
		add(IF_HASSUB, "if_hassub");
		add(IF_GETTOP, "if_gettop");
		add(IF_GETTARGETMASK, "if_gettargetmask");
		add(IF_GETOP, "if_getop");
		add(IF_GETOPBASE, "if_getopbase");
		add(IF_CALLONRESIZE, "if_callonresize");
		add(MES, "mes");
		add(ANIM, "anim");
		add(IF_CLOSE, "if_close");
		add(RESUME_COUNTDIALOG, "resume_countdialog");
		add(RESUME_NAMEDIALOG, "resume_namedialog");
		add(RESUME_STRINGDIALOG, "resume_stringdialog");
		add(OPPLAYER, "opplayer");
		add(IF_DRAGPICKUP, "if_dragpickup");
		add(CC_DRAGPICKUP, "cc_dragpickup");
		add(MOUSECAM, "mousecam");
		add(GETREMOVEROOFS, "getremoveroofs");
		add(SETREMOVEROOFS, "setremoveroofs");
		add(OPENURL, "openurl");
		add(RESUME_OBJDIALOG, "resume_objdialog");
		add(BUG_REPORT, "bug_report");
		add(SETSHIFTCLICKDROP, "setshiftclickdrop");
		add(SETSHOWMOUSEOVERTEXT, "setshowmouseovertext");
		add(RENDERSELF, "renderself");
		add(SETSHOWMOUSECROSS, "setshowmousecross");
		add(SETSHOWLOADINGMESSAGES, "setshowloadingmessages");
		add(SETTAPTODROP, "settaptodrop");
		add(GETTAPTODROP, "gettaptodrop");
		add(GETCANVASSIZE, "getcanvassize");
		add(SETHIDEUSERNAME, "sethideusername");
		add(GETHIDEUSERNAME, "gethideusername");
		add(SETREMEMBERUSERNAME, "setrememberusername");
		add(GETREMEMBERUSERNAME, "getrememberusername");
		add(SOUND_SYNTH, "sound_synth");
		add(SOUND_SONG, "sound_song");
		add(SOUND_JINGLE, "sound_jingle");
		add(CLIENTCLOCK, "clientclock");
		add(INV_GETOBJ, "inv_getobj");
		add(INV_GETNUM, "inv_getnum");
		add(INV_TOTAL, "inv_total");
		add(INV_SIZE, "inv_size");
		add(STAT, "stat");
		add(STAT_BASE, "stat_base");
		add(STAT_XP, "stat_xp");
		add(COORD, "coord");
		add(COORDX, "coordx");
		add(COORDZ, "coordz");
		add(COORDY, "coordy");
		add(MAP_MEMBERS, "map_members");
		add(INVOTHER_GETOBJ, "invother_getobj");
		add(INVOTHER_GETNUM, "invother_getnum");
		add(INVOTHER_TOTAL, "invother_total");
		add(STAFFMODLEVEL, "staffmodlevel");
		add(REBOOTTIMER, "reboottimer");
		add(MAP_WORLD, "map_world");
		add(RUNENERGY_VISIBLE, "runenergy_visible");
		add(RUNWEIGHT_VISIBLE, "runweight_visible");
		add(PLAYERMOD, "playermod");
		add(WORLDFLAGS, "worldflags");
		add(MOVECOORD, "movecoord");
		add(ENUM_STRING, "enum_string");
		add(ENUM, "enum");
		add(ENUM_GETOUTPUTCOUNT, "enum_getoutputcount");
		add(FRIEND_COUNT, "friend_count");
		add(FRIEND_GETNAME, "friend_getname");
		add(FRIEND_GETWORLD, "friend_getworld");
		add(FRIEND_GETRANK, "friend_getrank");
		add(FRIEND_SETRANK, "friend_setrank");
		add(FRIEND_ADD, "friend_add");
		add(FRIEND_DEL, "friend_del");
		add(IGNORE_ADD, "ignore_add");
		add(IGNORE_DEL, "ignore_del");
		add(FRIEND_TEST, "friend_test");
		add(CLAN_GETCHATDISPLAYNAME, "clan_getchatdisplayname");
		add(CLAN_GETCHATCOUNT, "clan_getchatcount");
		add(CLAN_GETCHATUSERNAME, "clan_getchatusername");
		add(CLAN_GETCHATUSERWORLD, "clan_getchatuserworld");
		add(CLAN_GETCHATUSERRANK, "clan_getchatuserrank");
		add(CLAN_GETCHATMINKICK, "clan_getchatminkick");
		add(CLAN_KICKUSER, "clan_kickuser");
		add(CLAN_GETCHATRANK, "clan_getchatrank");
		add(CLAN_JOINCHAT, "clan_joinchat");
		add(CLAN_LEAVECHAT, "clan_leavechat");
		add(IGNORE_COUNT, "ignore_count");
		add(IGNORE_GETNAME, "ignore_getname");
		add(IGNORE_TEST, "ignore_test");
		add(CLAN_ISSELF, "clan_isself");
		add(CLAN_GETCHATOWNERNAME, "clan_getchatownername");
		add(CLAN_ISFRIEND, "clan_isfriend");
		add(CLAN_ISIGNORE, "clan_isignore");
		add(STOCKMARKET_GETOFFERTYPE, "stockmarket_getoffertype");
		add(STOCKMARKET_GETOFFERITEM, "stockmarket_getofferitem");
		add(STOCKMARKET_GETOFFERPRICE, "stockmarket_getofferprice");
		add(STOCKMARKET_GETOFFERCOUNT, "stockmarket_getoffercount");
		add(STOCKMARKET_GETOFFERCOMPLETEDCOUNT, "stockmarket_getoffercompletedcount");
		add(STOCKMARKET_GETOFFERCOMPLETEDGOLD, "stockmarket_getoffercompletedgold");
		add(STOCKMARKET_ISOFFEREMPTY, "stockmarket_isofferempty");
		add(STOCKMARKET_ISOFFERSTABLE, "stockmarket_isofferstable");
		add(STOCKMARKET_ISOFFERFINISHED, "stockmarket_isofferfinished");
		add(STOCKMARKET_ISOFFERADDING, "stockmarket_isofferadding");
		add(TRADINGPOST_SORTBY_NAME, "tradingpost_sortby_name");
		add(TRADINGPOST_SORTBY_PRICE, "tradingpost_sortby_price");
		add(TRADINGPOST_SORTFILTERBY_WORLD, "tradingpost_sortfilterby_world");
		add(TRADINGPOST_SORTBY_AGE, "tradingpost_sortby_age");
		add(TRADINGPOST_SORTBY_COUNT, "tradingpost_sortby_count");
		add(TRADINGPOST_GETTOTALOFFERS, "tradingpost_gettotaloffers");
		add(TRADINGPOST_GETOFFERWORLD, "tradingpost_getofferworld");
		add(TRADINGPOST_GETOFFERNAME, "tradingpost_getoffername");
		add(TRADINGPOST_GETOFFERPREVIOUSNAME, "tradingpost_getofferpreviousname");
		add(TRADINGPOST_GETOFFERAGE, "tradingpost_getofferage");
		add(TRADINGPOST_GETOFFERCOUNT, "tradingpost_getoffercount");
		add(TRADINGPOST_GETOFFERPRICE, "tradingpost_getofferprice");
		add(TRADINGPOST_GETOFFERITEM, "tradingpost_getofferitem");
		add(ADD, "add");
		add(SUB, "sub");
		add(MULTIPLY, "multiply");
		add(DIV, "div");
		add(RANDOM, "random");
		add(RANDOMINC, "randominc");
		add(INTERPOLATE, "interpolate");
		add(ADDPERCENT, "addpercent");
		add(SETBIT, "setbit");
		add(CLEARBIT, "clearbit");
		add(TESTBIT, "testbit");
		add(MOD, "mod");
		add(POW, "pow");
		add(INVPOW, "invpow");
		add(AND, "and");
		add(OR, "or");
		add(SCALE, "scale");
		add(APPEND_NUM, "append_num");
		add(APPEND, "append");
		add(APPEND_SIGNNUM, "append_signnum");
		add(LOWERCASE, "lowercase");
		add(FROMDATE, "fromdate");
		add(TEXT_GENDER, "text_gender");
		add(TOSTRING, "tostring");
		add(COMPARE, "compare");
		add(PARAHEIGHT, "paraheight");
		add(PARAWIDTH, "parawidth");
		add(TEXT_SWITCH, "text_switch");
		add(ESCAPE, "escape");
		add(APPEND_CHAR, "append_char");
		add(CHAR_ISPRINTABLE, "char_isprintable");
		add(CHAR_ISALPHANUMERIC, "char_isalphanumeric");
		add(CHAR_ISALPHA, "char_isalpha");
		add(CHAR_ISNUMERIC, "char_isnumeric");
		add(STRING_LENGTH, "string_length");
		add(SUBSTRING, "substring");
		add(REMOVETAGS, "removetags");
		add(STRING_INDEXOF_CHAR, "string_indexof_char");
		add(STRING_INDEXOF_STRING, "string_indexof_string");
		add(OC_NAME, "oc_name");
		add(OC_OP, "oc_op");
		add(OC_IOP, "oc_iop");
		add(OC_COST, "oc_cost");
		add(OC_STACKABLE, "oc_stackable");
		add(OC_CERT, "oc_cert");
		add(OC_UNCERT, "oc_uncert");
		add(OC_MEMBERS, "oc_members");
		add(OC_PLACEHOLDER, "oc_placeholder");
		add(OC_UNPLACEHOLDER, "oc_unplaceholder");
		add(OC_FIND, "oc_find");
		add(OC_FINDNEXT, "oc_findnext");
		add(OC_FINDRESET, "oc_findreset");
		add(CHAT_GETFILTER_PUBLIC, "chat_getfilter_public");
		add(CHAT_SETFILTER, "chat_setfilter");
		add(CHAT_SENDABUSEREPORT, "chat_sendabusereport");
		add(CHAT_GETHISTORY_BYTYPEANDLINE, "chat_gethistory_bytypeandline");
		add(CHAT_GETHISTORY_BYUID, "chat_gethistory_byuid");
		add(CHAT_GETFILTER_PRIVATE, "chat_getfilter_private");
		add(CHAT_SENDPUBLIC, "chat_sendpublic");
		add(CHAT_SENDPRIVATE, "chat_sendprivate");
		add(CHAT_PLAYERNAME, "chat_playername");
		add(CHAT_GETFILTER_TRADE, "chat_getfilter_trade");
		add(CHAT_GETHISTORYLENGTH, "chat_gethistorylength");
		add(CHAT_GETNEXTUID, "chat_getnextuid");
		add(CHAT_GETPREVUID, "chat_getprevuid");
		add(DOCHEAT, "docheat");
		add(CHAT_SETMESSAGEFILTER, "chat_setmessagefilter");
		add(CHAT_GETMESSAGEFILTER, "chat_getmessagefilter");
		add(GETWINDOWMODE, "getwindowmode");
		add(SETWINDOWMODE, "setwindowmode");
		add(GETDEFAULTWINDOWMODE, "getdefaultwindowmode");
		add(SETDEFAULTWINDOWMODE, "setdefaultwindowmode");
		add(CAM_FORCEANGLE, "cam_forceangle");
		add(CAM_GETANGLE_XA, "cam_getangle_xa");
		add(CAM_GETANGLE_YA, "cam_getangle_ya");
		add(CAM_SETFOLLOWHEIGHT, "cam_setfollowheight");
		add(CAM_GETFOLLOWHEIGHT, "cam_getfollowheight");
		add(LOGOUT, "logout");
		add(VIEWPORT_SETFOV, "viewport_setfov");
		add(VIEWPORT_SETZOOM, "viewport_setzoom");
		add(VIEWPORT_CLAMPFOV, "viewport_clampfov");
		add(VIEWPORT_GETEFFECTIVESIZE, "viewport_geteffectivesize");
		add(VIEWPORT_GETZOOM, "viewport_getzoom");
		add(VIEWPORT_GETFOV, "viewport_getfov");
		add(WORLDLIST_FETCH, "worldlist_fetch");
		add(WORLDLIST_START, "worldlist_start");
		add(WORLDLIST_NEXT, "worldlist_next");
		add(WORLDLIST_SPECIFIC, "worldlist_specific");
		add(WORLDLIST_SORT, "worldlist_sort");
		add(SETFOLLOWEROPSLOWPRIORITY, "setfolloweropslowpriority");
		add(NC_PARAM, "nc_param");
		add(LC_PARAM, "lc_param");
		add(OC_PARAM, "oc_param");
		add(STRUCT_PARAM, "struct_param");
		add(ON_MOBILE, "on_mobile");
		add(CLIENTTYPE, "clienttype");
		add(BATTERYLEVEL, "batterylevel");
		add(BATTERYCHARGING, "batterycharging");
		add(WIFIAVAILABLE, "wifiavailable");
		add(WORLDMAP_GETMAPNAME, "worldmap_getmapname");
		add(WORLDMAP_SETMAP, "worldmap_setmap");
		add(WORLDMAP_GETZOOM, "worldmap_getzoom");
		add(WORLDMAP_SETZOOM, "worldmap_setzoom");
		add(WORLDMAP_ISLOADED, "worldmap_isloaded");
		add(WORLDMAP_JUMPTODISPLAYCOORD, "worldmap_jumptodisplaycoord");
		add(WORLDMAP_JUMPTODISPLAYCOORD_INSTANT, "worldmap_jumptodisplaycoord_instant");
		add(WORLDMAP_JUMPTOSOURCECOORD, "worldmap_jumptosourcecoord");
		add(WORLDMAP_JUMPTOSOURCECOORD_INSTANT, "worldmap_jumptosourcecoord_instant");
		add(WORLDMAP_GETDISPLAYPOSITION, "worldmap_getdisplayposition");
		add(WORLDMAP_GETCONFIGORIGIN, "worldmap_getconfigorigin");
		add(WORLDMAP_GETCONFIGSIZE, "worldmap_getconfigsize");
		add(WORLDMAP_GETCONFIGBOUNDS, "worldmap_getconfigbounds");
		add(WORLDMAP_GETCONFIGZOOM, "worldmap_getconfigzoom");
		add(WORLDMAP_GETCURRENTMAP, "worldmap_getcurrentmap");
		add(WORLDMAP_GETDISPLAYCOORD, "worldmap_getdisplaycoord");
		add(WORLDMAP_COORDINMAP, "worldmap_coordinmap");
		add(WORLDMAP_GETSIZE, "worldmap_getsize");
		add(WORLDMAP_PERPETUALFLASH, "worldmap_perpetualflash");
		add(WORLDMAP_FLASHELEMENT, "worldmap_flashelement");
		add(WORLDMAP_FLASHELEMENTCATEGORY, "worldmap_flashelementcategory");
		add(WORLDMAP_STOPCURRENTFLASHES, "worldmap_stopcurrentflashes");
		add(WORLDMAP_DISABLEELEMENTS, "worldmap_disableelements");
		add(WORLDMAP_DISABLEELEMENT, "worldmap_disableelement");
		add(WORLDMAP_DISABLEELEMENTCATEGORY, "worldmap_disableelementcategory");
		add(WORLDMAP_GETDISABLEELEMENTS, "worldmap_getdisableelements");
		add(WORLDMAP_GETDISABLEELEMENT, "worldmap_getdisableelement");
		add(WORLDMAP_GETDISABLEELEMENTCATEGORY, "worldmap_getdisableelementcategory");
		add(WORLDMAP_LISTELEMENT_START, "worldmap_listelement_start");
		add(WORLDMAP_LISTELEMENT_NEXT, "worldmap_listelement_next");
		add(MEC_TEXT, "mec_text");
		add(MEC_TEXTSIZE, "mec_textsize");
		add(MEC_CATEGORY, "mec_category");
		add(MEC_SPRITE, "mec_sprite");
	}

	protected void add(int opcode, String name)
	{
		Instruction i = new Instruction(opcode);
		i.setName(name);

		assert instructions.containsKey(opcode) == false;
		instructions.put(opcode, i);

		if (name != null)
		{
			assert instructionsByName.containsKey(name) == false;
			instructionsByName.put(name, i);
		}
	}

	public Instruction find(int opcode)
	{
		return instructions.get(opcode);
	}

	public Instruction find(String name)
	{
		return instructionsByName.get(name);
	}
}
