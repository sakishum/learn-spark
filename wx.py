#!/usr/bin/env python
# -*- coding:utf-8 -*-
import itchat
itchat.auto_login(hotReload=True)
itchat.dump_login_status()

for friend in itchat.get_friends():
    print(friend)