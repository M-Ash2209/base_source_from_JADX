webpackJsonp([3],{2041:function(e,n,t){"use strict";function View_AddonMessagesConfirmedContactsComponent_2(e){return s._58(0,[(e()(),s._32(0,0,null,null,1,"core-icon",[["item-end",""],["name","fa-ban"]],null,null,null,E.b,E.a)),s._31(1,704512,null,0,V.a,[s.t,x.a],{name:[0,"name"]},null)],function(e,n){e(n,1,0,"fa-ban")},null)}function View_AddonMessagesConfirmedContactsComponent_1(e){return s._58(0,[(e()(),s._32(0,0,null,null,16,"a",[["class","addon-messages-conversation-item item item-block"],["ion-item",""],["text-wrap",""]],[[8,"title",0],[2,"core-split-item-selected",null]],[[null,"click"]],function(e,n,t){var s=!0;if("click"===n){s=!1!==e.component.selectUser(e.context.$implicit.id)&&s}return s},k.b,k.a)),s._31(1,1097728,null,3,T.a,[N.a,x.a,s.t,s.W,[2,L.a]],null,null),s._53(335544320,2,{contentLabel:0}),s._53(603979776,3,{_buttons:1}),s._53(603979776,4,{_icons:1}),s._31(5,16384,null,0,W.a,[],null,null),(e()(),s._56(-1,2,["\n                "])),(e()(),s._32(7,0,null,0,2,"ion-avatar",[["core-user-avatar",""],["item-start",""]],null,null,null,$.b,$.a)),s._31(8,16384,null,0,F.a,[],null,null),s._31(9,770048,null,0,H.a,[G.a,i.b,B.b,J.b,l.b,[2,c.a]],{user:[0,"user"],linkProfile:[1,"linkProfile"],checkOnline:[2,"checkOnline"]},null),(e()(),s._56(-1,2,["\n                "])),(e()(),s._32(11,0,null,2,4,"h2",[],null,null,null,null,null)),(e()(),s._56(12,null,["\n                    ","\n                    "])),(e()(),s._27(16777216,null,null,1,null,View_AddonMessagesConfirmedContactsComponent_2)),s._31(14,16384,null,0,z.k,[s._12,s._7],{ngIf:[0,"ngIf"]},null),(e()(),s._56(-1,null,["\n                "])),(e()(),s._56(-1,2,["\n            "]))],function(e,n){e(n,9,0,n.context.$implicit,!1,n.context.$implicit.showonlinestatus);e(n,14,0,n.context.$implicit.isblocked)},function(e,n){e(n,0,0,n.context.$implicit.fullname,n.context.$implicit.id==n.component.selectedUserId);e(n,12,0,n.context.$implicit.fullname)})}function View_AddonMessagesConfirmedContactsComponent_3(e){return s._58(0,[(e()(),s._32(0,0,null,null,2,"core-empty-box",[["icon","person"]],null,null,null,K.b,K.a)),s._31(1,49152,null,0,Q.a,[],{message:[0,"message"],icon:[1,"icon"]},null),s._48(131072,Z.a,[X.a,s.j])],function(e,n){e(n,1,0,s._57(n,1,0,s._45(n,2).transform("addon.messages.nocontactsgetstarted")),"person")},null)}function View_AddonMessagesConfirmedContactsComponent_0(e){return s._58(0,[s._53(402653184,1,{content:0}),(e()(),s._32(1,0,null,null,27,"ion-content",[],[[2,"statusbar-padding",null],[2,"has-refresher",null]],null,null,Y.b,Y.a)),s._31(2,4374528,[[1,4]],0,ee.a,[x.a,ne.a,te.a,s.t,s.W,se.a,oe.a,s.N,[2,ae.a],[2,G.a]],null,null),(e()(),s._56(-1,1,["\n    "])),(e()(),s._32(4,0,null,2,6,"ion-refresher",[],[[2,"refresher-active",null],[4,"top",null]],[[null,"ionRefresh"]],function(e,n,t){var s=!0;if("ionRefresh"===n){s=!1!==e.component.refreshData(t)&&s}return s},null,null)),s._31(5,212992,null,0,le.a,[ne.a,ee.a,s.N,ie.l],{enabled:[0,"enabled"]},{ionRefresh:"ionRefresh"}),(e()(),s._56(-1,null,["\n        "])),(e()(),s._32(7,0,null,null,2,"ion-refresher-content",[],[[1,"state",0]],null,null,re.b,re.a)),s._31(8,114688,null,0,ce.a,[le.a,x.a],{pullingText:[0,"pullingText"]},null),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,null,["\n    "])),(e()(),s._56(-1,1,["\n    "])),(e()(),s._32(12,0,null,1,15,"core-loading",[["class","core-loading-center"]],null,null,null,ue.b,ue.a)),s._31(13,638976,null,0,de.a,[X.a,s.t,l.b,B.b],{hideUntil:[0,"hideUntil"]},null),(e()(),s._56(-1,0,["\n        "])),(e()(),s._32(15,0,null,0,5,"ion-list",[["no-margin",""]],null,null,null,null,null)),s._31(16,16384,null,0,fe.a,[x.a,s.t,s.W,ne.a,ie.l,te.a],null,null),(e()(),s._56(-1,null,["\n            "])),(e()(),s._27(16777216,null,null,1,null,View_AddonMessagesConfirmedContactsComponent_1)),s._31(19,802816,null,0,z.j,[s._12,s._7,s.E],{ngForOf:[0,"ngForOf"]},null),(e()(),s._56(-1,null,["\n        "])),(e()(),s._56(-1,0,["\n        "])),(e()(),s._27(16777216,null,0,1,null,View_AddonMessagesConfirmedContactsComponent_3)),s._31(23,16384,null,0,z.k,[s._12,s._7],{ngIf:[0,"ngIf"]},null),(e()(),s._56(-1,0,["\n        "])),(e()(),s._32(25,0,null,0,1,"core-infinite-loading",[["position","bottom"]],null,[[null,"action"]],function(e,n,t){var s=!0;if("action"===n){s=!1!==e.component.loadMore(t)&&s}return s},he.b,he.a)),s._31(26,573440,null,0,_e.a,[[2,ee.a],ge.b],{enabled:[0,"enabled"],error:[1,"error"],position:[2,"position"]},{action:"action"}),(e()(),s._56(-1,0,["\n    "])),(e()(),s._56(-1,1,["\n"])),(e()(),s._56(-1,null,["\n"]))],function(e,n){var t=n.component;e(n,5,0,t.loaded);e(n,8,0,s._35(1,"",s._57(n,8,0,s._45(n,9).transform("core.pulltorefresh")),""));e(n,13,0,t.loaded);e(n,19,0,t.contacts);e(n,23,0,!t.contacts.length);e(n,26,0,t.canLoadMore,t.loadMoreError,"bottom")},function(e,n){e(n,1,0,s._45(n,2).statusbarPadding,s._45(n,2)._hasRefresher);e(n,4,0,"inactive"!==s._45(n,5).state,s._45(n,5)._top);e(n,7,0,s._45(n,8).r.state)})}function View_AddonMessagesContactRequestsComponent_2(e){return s._58(0,[(e()(),s._32(0,0,null,null,2,"p",[],null,null,null,null,null)),(e()(),s._56(1,null,["",""])),s._48(131072,Z.a,[X.a,s.j])],null,function(e,n){e(n,1,0,s._57(n,1,0,s._45(n,2).transform("addon.messages.wouldliketocontactyou")))})}function View_AddonMessagesContactRequestsComponent_1(e){return s._58(0,[(e()(),s._32(0,0,null,null,16,"a",[["class","addon-messages-conversation-item item item-block"],["ion-item",""],["text-wrap",""]],[[8,"title",0],[2,"core-split-item-selected",null]],[[null,"click"]],function(e,n,t){var s=!0;if("click"===n){s=!1!==e.component.selectUser(e.context.$implicit.id)&&s}return s},k.b,k.a)),s._31(1,1097728,null,3,T.a,[N.a,x.a,s.t,s.W,[2,L.a]],null,null),s._53(335544320,2,{contentLabel:0}),s._53(603979776,3,{_buttons:1}),s._53(603979776,4,{_icons:1}),s._31(5,16384,null,0,W.a,[],null,null),(e()(),s._56(-1,2,["\n                "])),(e()(),s._32(7,0,null,0,2,"ion-avatar",[["core-user-avatar",""],["item-start",""]],null,null,null,$.b,$.a)),s._31(8,16384,null,0,F.a,[],null,null),s._31(9,770048,null,0,H.a,[G.a,i.b,B.b,J.b,l.b,[2,c.a]],{user:[0,"user"],linkProfile:[1,"linkProfile"]},null),(e()(),s._56(-1,2,["\n                "])),(e()(),s._32(11,0,null,2,1,"h2",[],null,null,null,null,null)),(e()(),s._56(12,null,["",""])),(e()(),s._56(-1,2,["\n                "])),(e()(),s._27(16777216,null,2,1,null,View_AddonMessagesContactRequestsComponent_2)),s._31(15,16384,null,0,z.k,[s._12,s._7],{ngIf:[0,"ngIf"]},null),(e()(),s._56(-1,2,["\n            "]))],function(e,n){e(n,9,0,n.context.$implicit,!1);e(n,15,0,!n.context.$implicit.iscontact&&!n.context.$implicit.confirmedOrDeclined)},function(e,n){e(n,0,0,n.context.$implicit.fullname,n.context.$implicit.id==n.component.selectedUserId);e(n,12,0,n.context.$implicit.fullname)})}function View_AddonMessagesContactRequestsComponent_3(e){return s._58(0,[(e()(),s._32(0,0,null,null,2,"core-empty-box",[["icon","person"]],null,null,null,K.b,K.a)),s._31(1,49152,null,0,Q.a,[],{message:[0,"message"],icon:[1,"icon"]},null),s._48(131072,Z.a,[X.a,s.j])],function(e,n){e(n,1,0,s._57(n,1,0,s._45(n,2).transform("addon.messages.nocontactrequests")),"person")},null)}function View_AddonMessagesContactRequestsComponent_0(e){return s._58(0,[s._53(402653184,1,{content:0}),(e()(),s._32(1,0,null,null,27,"ion-content",[],[[2,"statusbar-padding",null],[2,"has-refresher",null]],null,null,Y.b,Y.a)),s._31(2,4374528,[[1,4]],0,ee.a,[x.a,ne.a,te.a,s.t,s.W,se.a,oe.a,s.N,[2,ae.a],[2,G.a]],null,null),(e()(),s._56(-1,1,["\n    "])),(e()(),s._32(4,0,null,2,6,"ion-refresher",[],[[2,"refresher-active",null],[4,"top",null]],[[null,"ionRefresh"]],function(e,n,t){var s=!0;if("ionRefresh"===n){s=!1!==e.component.refreshData(t)&&s}return s},null,null)),s._31(5,212992,null,0,le.a,[ne.a,ee.a,s.N,ie.l],{enabled:[0,"enabled"]},{ionRefresh:"ionRefresh"}),(e()(),s._56(-1,null,["\n        "])),(e()(),s._32(7,0,null,null,2,"ion-refresher-content",[],[[1,"state",0]],null,null,re.b,re.a)),s._31(8,114688,null,0,ce.a,[le.a,x.a],{pullingText:[0,"pullingText"]},null),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,null,["\n    "])),(e()(),s._56(-1,1,["\n    "])),(e()(),s._32(12,0,null,1,15,"core-loading",[["class","core-loading-center"]],null,null,null,ue.b,ue.a)),s._31(13,638976,null,0,de.a,[X.a,s.t,l.b,B.b],{hideUntil:[0,"hideUntil"]},null),(e()(),s._56(-1,0,["\n        "])),(e()(),s._32(15,0,null,0,5,"ion-list",[["no-margin",""]],null,null,null,null,null)),s._31(16,16384,null,0,fe.a,[x.a,s.t,s.W,ne.a,ie.l,te.a],null,null),(e()(),s._56(-1,null,["\n            "])),(e()(),s._27(16777216,null,null,1,null,View_AddonMessagesContactRequestsComponent_1)),s._31(19,802816,null,0,z.j,[s._12,s._7,s.E],{ngForOf:[0,"ngForOf"]},null),(e()(),s._56(-1,null,["\n        "])),(e()(),s._56(-1,0,["\n        "])),(e()(),s._27(16777216,null,0,1,null,View_AddonMessagesContactRequestsComponent_3)),s._31(23,16384,null,0,z.k,[s._12,s._7],{ngIf:[0,"ngIf"]},null),(e()(),s._56(-1,0,["\n        "])),(e()(),s._32(25,0,null,0,1,"core-infinite-loading",[["position","bottom"]],null,[[null,"action"]],function(e,n,t){var s=!0;if("action"===n){s=!1!==e.component.loadMore(t)&&s}return s},he.b,he.a)),s._31(26,573440,null,0,_e.a,[[2,ee.a],ge.b],{enabled:[0,"enabled"],error:[1,"error"],position:[2,"position"]},{action:"action"}),(e()(),s._56(-1,0,["\n    "])),(e()(),s._56(-1,1,["\n"])),(e()(),s._56(-1,null,["\n"]))],function(e,n){var t=n.component;e(n,5,0,t.loaded);e(n,8,0,s._35(1,"",s._57(n,8,0,s._45(n,9).transform("core.pulltorefresh")),""));e(n,13,0,t.loaded);e(n,19,0,t.requests);e(n,23,0,!t.requests.length);e(n,26,0,t.canLoadMore,t.loadMoreError,"bottom")},function(e,n){e(n,1,0,s._45(n,2).statusbarPadding,s._45(n,2)._hasRefresher);e(n,4,0,"inactive"!==s._45(n,5).state,s._45(n,5)._top);e(n,7,0,s._45(n,8).r.state)})}function View_AddonMessagesContactsPage_1(e){return s._58(0,[(e()(),s._56(-1,null,["\n                    "])),(e()(),s._32(1,0,null,null,1,"addon-messages-confirmed-contacts",[],null,[[null,"onUserSelected"]],function(e,n,t){var s=!0;if("onUserSelected"===n){s=!1!==e.component.selectUser("contacts",t.userId,t.onInit)&&s}return s},View_AddonMessagesConfirmedContactsComponent_0,me)),s._31(2,245760,null,0,pe.a,[ge.b,l.b,i.b,r.a],null,{onUserSelected:"onUserSelected"}),(e()(),s._56(-1,null,["\n                "]))],function(e,n){e(n,2,0)},null)}function View_AddonMessagesContactsPage_2(e){return s._58(0,[(e()(),s._56(-1,null,["\n                    "])),(e()(),s._32(1,0,null,null,1,"addon-messages-contact-requests",[],null,[[null,"onUserSelected"]],function(e,n,t){var s=!0;if("onUserSelected"===n){s=!1!==e.component.selectUser("requests",t.userId,t.onInit)&&s}return s},View_AddonMessagesContactRequestsComponent_0,Ce)),s._31(2,245760,null,0,be.a,[ge.b,l.b,i.b,r.a],null,{onUserSelected:"onUserSelected"}),(e()(),s._56(-1,null,["\n                "]))],function(e,n){e(n,2,0)},null)}function View_AddonMessagesContactsPage_0(e){return s._58(0,[s._53(402653184,1,{splitviewCtrl:0}),s._53(402653184,2,{tabsComponent:0}),(e()(),s._32(2,0,null,null,29,"ion-header",[],null,null,null,null,null)),s._31(3,16384,null,0,ve.a,[x.a,s.t,s.W,[2,ae.a]],null,null),(e()(),s._56(-1,null,["\n    "])),(e()(),s._32(5,0,null,null,25,"ion-navbar",[["class","toolbar"],["core-back-button",""]],[[8,"hidden",0],[2,"statusbar-padding",null]],null,null,Me.b,Me.a)),s._31(6,49152,null,0,ye.a,[se.a,[2,ae.a],[2,G.a],x.a,s.t,s.W],null,null),s._31(7,212992,null,0,Ae.a,[ye.a,X.a,l.b],null,null),(e()(),s._56(-1,3,["\n        "])),(e()(),s._32(9,0,null,3,3,"ion-title",[],null,null,null,Ie.b,Ie.a)),s._31(10,49152,null,0,Re.a,[x.a,s.t,s.W,[2,Oe.a],[2,ye.a]],null,null),(e()(),s._56(11,0,["",""])),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,3,["\n        "])),(e()(),s._32(14,0,null,2,15,"ion-buttons",[["end",""]],null,null,null,null,null)),s._31(15,16384,null,1,Ue.a,[x.a,s.t,s.W,[2,Oe.a],[2,ye.a]],null,null),s._53(603979776,3,{_buttons:1}),(e()(),s._56(-1,null,["\n            "])),(e()(),s._32(18,0,null,null,6,"button",[["icon-only",""],["ion-button",""]],[[1,"aria-label",0]],[[null,"click"]],function(e,n,t){var s=!0;if("click"===n){s=!1!==e.component.gotoSearch()&&s}return s},Pe.b,Pe.a)),s._31(19,1097728,[[3,4]],0,we.a,[[8,""],x.a,s.t,s.W],null,null),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,0,["\n                "])),(e()(),s._32(22,0,null,0,1,"ion-icon",[["name","search"],["role","img"]],[[2,"hide",null]],null,null,null,null)),s._31(23,147456,null,0,De.a,[x.a,s.t,s.W],{name:[0,"name"]},null),(e()(),s._56(-1,0,["\n            "])),(e()(),s._56(-1,null,["\n            "])),(e()(),s._56(-1,null,["\n            "])),(e()(),s._32(27,0,null,null,1,"core-context-menu",[],null,null,null,Se.b,Se.a)),s._31(28,245760,null,0,qe.a,[X.a,je.a,s.t,ge.b,[2,Ee.a],B.b],null,null),(e()(),s._56(-1,null,["\n        "])),(e()(),s._56(-1,3,["\n    "])),(e()(),s._56(-1,null,["\n"])),(e()(),s._56(-1,null,["\n"])),(e()(),s._32(33,0,null,null,28,"core-split-view",[],null,null,null,Ve.b,Ve.a)),s._31(34,245760,[[1,4]],0,c.a,[[2,G.a],s.t,xe.b,ne.a,X.a],null,null),(e()(),s._56(-1,0,["\n    "])),(e()(),s._32(36,0,null,0,24,"ion-content",[],[[2,"statusbar-padding",null],[2,"has-refresher",null]],null,null,Y.b,Y.a)),s._31(37,4374528,null,0,ee.a,[x.a,ne.a,te.a,s.t,s.W,se.a,oe.a,s.N,[2,ae.a],[2,G.a]],null,null),(e()(),s._56(-1,1,["\n        "])),(e()(),s._32(39,0,null,1,20,"core-tabs",[],null,null,null,ke.b,ke.a)),s._31(40,4964352,[[2,4]],0,u.a,[s.t,ee.a,ge.b,J.b,Te.b,ne.a,X.a],null,null),(e()(),s._56(-1,0,["\n            "])),(e()(),s._32(42,0,null,0,7,"core-tab",[],null,[[null,"ionSelect"]],function(e,n,t){var s=!0;if("ionSelect"===n){s=!1!==e.component.selectUser("contacts")&&s}return s},Ne.b,Ne.a)),s._31(43,245760,null,2,Ee.a,[u.a,s.t,ge.b,B.b],{title:[0,"title"]},{ionSelect:"ionSelect"}),s._53(335544320,4,{template:0}),s._53(335544320,5,{content:0}),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,null,["\n                "])),(e()(),s._27(0,[[4,2]],null,0,null,View_AddonMessagesContactsPage_1)),(e()(),s._56(-1,null,["\n            "])),(e()(),s._56(-1,0,["\n            "])),(e()(),s._32(51,0,null,0,7,"core-tab",[],null,[[null,"ionSelect"]],function(e,n,t){var s=!0;if("ionSelect"===n){s=!1!==e.component.selectUser("requests")&&s}return s},Ne.b,Ne.a)),s._31(52,245760,null,2,Ee.a,[u.a,s.t,ge.b,B.b],{title:[0,"title"],badge:[1,"badge"]},{ionSelect:"ionSelect"}),s._53(335544320,6,{template:0}),s._53(335544320,7,{content:0}),s._48(131072,Z.a,[X.a,s.j]),(e()(),s._56(-1,null,["\n                 "])),(e()(),s._27(0,[[6,2]],null,0,null,View_AddonMessagesContactsPage_2)),(e()(),s._56(-1,null,["\n            "])),(e()(),s._56(-1,0,["\n        "])),(e()(),s._56(-1,1,["\n    "])),(e()(),s._56(-1,0,["\n"]))],function(e,n){var t=n.component;e(n,7,0);e(n,23,0,"search"),e(n,28,0),e(n,34,0),e(n,40,0);e(n,43,0,s._57(n,43,0,s._45(n,46).transform("addon.messages.contacts")));e(n,52,0,s._57(n,52,0,s._45(n,55).transform("addon.messages.requests")),t.contactRequestsCount)},function(e,n){e(n,5,0,s._45(n,6)._hidden,s._45(n,6)._sbPadding);e(n,11,0,s._57(n,11,0,s._45(n,12).transform("addon.messages.contacts")));e(n,18,0,s._57(n,18,0,s._45(n,20).transform("addon.messages.search")));e(n,22,0,s._45(n,23)._hidden);e(n,36,0,s._45(n,37).statusbarPadding,s._45(n,37)._hasRefresher)})}Object.defineProperty(n,"__esModule",{value:!0});var s=t(0),o=t(5),a=t(3),l=t(8),i=t(1),r=t(167),c=t(29),u=t(205),d=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},f=this&&this.__metadata||function(e,n){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,n)},h=function(){function AddonMessagesContactsPage(e,n,t,s){var o=this;this.navCtrl=t,this.messagesProvider=s,this.contactRequestsCount=0,this.selectedUserId={contacts:null,requests:null},this.siteId=n.getCurrentSiteId(),this.contactRequestsCountObserver=e.on(r.a.CONTACT_REQUESTS_COUNT_EVENT,function(e){o.contactRequestsCount=e.count},this.siteId)}return AddonMessagesContactsPage.prototype.ngOnInit=function(){this.messagesProvider.getContactRequestsCount(this.siteId)},AddonMessagesContactsPage.prototype.gotoSearch=function(){this.navCtrl.push("AddonMessagesSearchPage")},AddonMessagesContactsPage.prototype.ionViewDidEnter=function(){this.splitviewCtrl.isOn()||(this.selectedUserId.contacts=null,this.selectedUserId.requests=null),this.tabsComponent&&this.tabsComponent.ionViewDidEnter()},AddonMessagesContactsPage.prototype.ionViewDidLeave=function(){this.tabsComponent&&this.tabsComponent.ionViewDidLeave()},AddonMessagesContactsPage.prototype.selectUser=function(e,n,t){void 0===t&&(t=!1),!(n=n||this.selectedUserId[e])||n==this.conversationUserId&&this.splitviewCtrl.isOn()||t&&!this.splitviewCtrl.isOn()||(this.conversationUserId=n,this.selectedUserId[e]=n,this.splitviewCtrl.push("AddonMessagesDiscussionPage",{userId:n}))},AddonMessagesContactsPage.prototype.ngOnDestroy=function(){this.contactRequestsCountObserver&&this.contactRequestsCountObserver.off()},d([Object(s._10)(c.a),f("design:type",c.a)],AddonMessagesContactsPage.prototype,"splitviewCtrl",void 0),d([Object(s._10)(u.a),f("design:type",u.a)],AddonMessagesContactsPage.prototype,"tabsComponent",void 0),AddonMessagesContactsPage=d([Object(s.m)({selector:"page-addon-messages-contacts",templateUrl:"contacts.html"}),f("design:paramtypes",[l.b,i.b,o.s,r.a])],AddonMessagesContactsPage)}(),_=t(26),g=t(32),p=t(111),m=t(2181),b=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},C=function(){function AddonMessagesContactsPageModule(){}return AddonMessagesContactsPageModule=b([Object(s.J)({declarations:[h],imports:[_.a,g.a,p.a,m.a,o.l.forChild(h),a.b.forChild()]})],AddonMessagesContactsPageModule)}(),v=t(1575),M=t(1576),y=t(1577),A=t(1578),I=t(1579),R=t(1580),O=t(1581),U=t(1582),P=t(1583),w=t(1584),D=t(1585),S=t(1586),q=t(1587),j=t(498),E=t(85),V=t(78),x=t(9),k=t(31),T=t(22),N=t(21),L=t(27),W=t(33),$=t(220),F=t(166),H=t(187),G=t(20),B=t(2),J=t(12),z=t(7),K=t(127),Q=t(118),Z=t(25),X=t(18),Y=t(186),ee=t(28),ne=t(16),te=t(34),se=t(35),oe=t(112),ae=t(41),le=t(165),ie=t(45),re=t(219),ce=t(179),ue=t(57),de=t(54),fe=t(87),he=t(389),_e=t(285),ge=t(4),pe=t(2183),me=s._30({encapsulation:2,styles:[],data:{}}),be=(s._28("addon-messages-confirmed-contacts",pe.a,function View_AddonMessagesConfirmedContactsComponent_Host_0(e){return s._58(0,[(e()(),s._32(0,0,null,null,1,"addon-messages-confirmed-contacts",[],null,null,null,View_AddonMessagesConfirmedContactsComponent_0,me)),s._31(1,245760,null,0,pe.a,[ge.b,l.b,i.b,r.a],null,null)],function(e,n){e(n,1,0)},null)},{},{onUserSelected:"onUserSelected"},[]),t(2184)),Ce=s._30({encapsulation:2,styles:[],data:{}}),ve=(s._28("addon-messages-contact-requests",be.a,function View_AddonMessagesContactRequestsComponent_Host_0(e){return s._58(0,[(e()(),s._32(0,0,null,null,1,"addon-messages-contact-requests",[],null,null,null,View_AddonMessagesContactRequestsComponent_0,Ce)),s._31(1,245760,null,0,be.a,[ge.b,l.b,i.b,r.a],null,null)],function(e,n){e(n,1,0)},null)},{},{onUserSelected:"onUserSelected"},[]),t(387)),Me=t(753),ye=t(218),Ae=t(496),Ie=t(754),Re=t(329),Oe=t(255),Ue=t(388),Pe=t(47),we=t(44),De=t(48),Se=t(94),qe=t(81),je=t(72),Ee=t(80),Ve=t(497),xe=t(70),ke=t(760),Te=t(129),Ne=t(761),Le=s._30({encapsulation:2,styles:[],data:{}}),We=s._28("page-addon-messages-contacts",h,function View_AddonMessagesContactsPage_Host_0(e){return s._58(0,[(e()(),s._32(0,0,null,null,1,"page-addon-messages-contacts",[],null,null,null,View_AddonMessagesContactsPage_0,Le)),s._31(1,245760,null,0,h,[l.b,i.b,G.a,r.a],null,null)],function(e,n){e(n,1,0)},null)},{},{},[]),$e=t(23),Fe=t(383),He=t(384),Ge=t(386),Be=t(385),Je=t(495),ze=t(752),Ke=t(330),Qe=t(281);t.d(n,"AddonMessagesContactsPageModuleNgFactory",function(){return Ze});var Ze=s._29(C,[],function(e){return s._41([s._42(512,s.o,s._22,[[8,[v.a,M.a,y.a,A.a,I.a,R.a,O.a,U.a,P.a,w.a,D.a,S.a,q.a,j.a,We]],[3,s.o],s.L]),s._42(4608,z.m,z.l,[s.G,[2,z.w]]),s._42(4608,$e.x,$e.x,[]),s._42(4608,$e.d,$e.d,[]),s._42(4608,Fe.b,Fe.a,[]),s._42(4608,He.a,He.b,[]),s._42(4608,Ge.b,Ge.a,[]),s._42(4608,Be.b,Be.a,[]),s._42(4608,X.a,X.a,[Je.a,Fe.b,He.a,Ge.b,Be.b,X.b,X.c]),s._42(512,z.b,z.b,[]),s._42(512,$e.v,$e.v,[]),s._42(512,$e.i,$e.i,[]),s._42(512,$e.s,$e.s,[]),s._42(512,ze.a,ze.a,[]),s._42(512,a.b,a.b,[]),s._42(512,g.a,g.a,[]),s._42(512,p.a,p.a,[]),s._42(512,_.a,_.a,[]),s._42(512,Ke.a,Ke.a,[]),s._42(512,m.a,m.a,[]),s._42(512,ze.b,ze.b,[]),s._42(512,C,C,[]),s._42(256,X.c,void 0,[]),s._42(256,X.b,void 0,[]),s._42(256,Qe.a,h,[])])})},2181:function(e,n,t){"use strict";t.d(n,"a",function(){return p});var s=t(0),o=t(7),a=t(5),l=t(3),i=t(26),r=t(32),c=t(111),u=t(330),d=t(2182),f=t(2183),h=t(2184),_=t(2185),g=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},p=function(){function AddonMessagesComponentsModule(){}return AddonMessagesComponentsModule=g([Object(s.J)({declarations:[d.a,f.a,h.a,_.a],imports:[o.b,a.k,l.b.forChild(),i.a,r.a,c.a,u.a],providers:[],exports:[d.a,f.a,h.a,_.a]})],AddonMessagesComponentsModule)}()},2182:function(e,n,t){"use strict";t.d(n,"a",function(){return g});var s=t(0),o=t(5),a=t(3),l=t(8),i=t(1),r=t(167),c=t(4),u=t(2),d=t(12),f=t(148),h=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},_=this&&this.__metadata||function(e,n){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,n)},g=function(){function AddonMessagesDiscussionsComponent(e,n,t,s,o,a,l,i,c,u){var d=this;this.eventsProvider=e,this.messagesProvider=s,this.domUtils=o,this.appProvider=l,this.utils=c,this.loaded=!1,this.search={enabled:!1,showResults:!1,results:[],loading:"",text:""},this.search.loading=t.instant("core.searching"),this.loadingMessages=t.instant("core.loading"),this.siteId=n.getCurrentSiteId(),this.newMessagesObserver=e.on(r.a.NEW_MESSAGE_EVENT,function(e){if(e.userId&&d.discussions){var n=d.discussions.find(function(n){return n.message.user==e.userId});void 0===n?(d.loaded=!1,d.refreshData().finally(function(){d.loaded=!0})):(n.message.message=e.message,n.message.timecreated=e.timecreated)}},this.siteId),this.readChangedObserver=e.on(r.a.READ_CHANGED_EVENT,function(e){if(e.userId&&d.discussions){var n=d.discussions.find(function(n){return n.message.user==e.userId});void 0!==n&&(n.unread=!1,d.messagesProvider.invalidateConversations(d.siteId),d.messagesProvider.refreshUnreadConversationCounts(d.siteId))}},this.siteId),this.appResumeSubscription=i.resume.subscribe(function(){d.loaded&&(d.loaded=!1,d.refreshData())}),this.discussionUserId=a.get("discussionUserId")||!1,this.pushObserver=u.on("receive").subscribe(function(e){c.isFalseOrZero(e.notif)&&e.site==d.siteId&&d.refreshData(null,!1)})}return AddonMessagesDiscussionsComponent.prototype.ngOnInit=function(){var e=this;this.discussionUserId&&this.gotoDiscussion(this.discussionUserId),this.fetchData().then(function(){!e.discussionUserId&&e.discussions.length>0&&e.gotoDiscussion(e.discussions[0].message.user,void 0,!0)})},AddonMessagesDiscussionsComponent.prototype.refreshData=function(e,n){var t=this;void 0===n&&(n=!0);var s=[];return s.push(this.messagesProvider.invalidateDiscussionsCache(this.siteId)),n&&s.push(this.messagesProvider.invalidateUnreadConversationCounts(this.siteId)),this.utils.allPromises(s).finally(function(){return t.fetchData().finally(function(){e&&e.complete()})})},AddonMessagesDiscussionsComponent.prototype.fetchData=function(){var e=this;this.loadingMessage=this.loadingMessages,this.search.enabled=this.messagesProvider.isSearchMessagesEnabled();var n=[];return n.push(this.messagesProvider.getDiscussions(this.siteId).then(function(n){var t=[];for(var s in n)n[s].unread=!!n[s].unread,t.push(n[s]);e.discussions=t.sort(function(e,n){return n.message.timecreated-e.message.timecreated})})),n.push(this.messagesProvider.getUnreadConversationCounts(this.siteId)),Promise.all(n).catch(function(n){e.domUtils.showErrorModalDefault(n,"addon.messages.errorwhileretrievingdiscussions",!0)}).finally(function(){e.loaded=!0})},AddonMessagesDiscussionsComponent.prototype.clearSearch=function(){var e=this;this.loaded=!1,this.search.showResults=!1,this.search.text="",this.fetchData().finally(function(){e.loaded=!0})},AddonMessagesDiscussionsComponent.prototype.searchMessage=function(e){var n=this;return this.appProvider.closeKeyboard(),this.loaded=!1,this.loadingMessage=this.search.loading,this.messagesProvider.searchMessages(e,void 0,void 0,void 0,this.siteId).then(function(e){n.search.showResults=!0,n.search.results=e.messages}).catch(function(e){n.domUtils.showErrorModalDefault(e,"addon.messages.errorwhileretrievingmessages",!0)}).finally(function(){n.loaded=!0})},AddonMessagesDiscussionsComponent.prototype.gotoDiscussion=function(e,n,t){void 0===t&&(t=!1),this.discussionUserId=e;var s={discussion:e,onlyWithSplitView:t};n&&(s.message=n),this.eventsProvider.trigger(r.a.SPLIT_VIEW_LOAD_EVENT,s,this.siteId)},AddonMessagesDiscussionsComponent.prototype.ngOnDestroy=function(){this.newMessagesObserver&&this.newMessagesObserver.off(),this.readChangedObserver&&this.readChangedObserver.off(),this.cronObserver&&this.cronObserver.off(),this.appResumeSubscription&&this.appResumeSubscription.unsubscribe(),this.pushObserver&&this.pushObserver.unsubscribe()},AddonMessagesDiscussionsComponent=h([Object(s.m)({selector:"addon-messages-discussions",templateUrl:"addon-messages-discussions.html"}),_("design:paramtypes",[l.b,i.b,a.c,r.a,c.b,o.t,d.b,o.v,u.b,f.a])],AddonMessagesDiscussionsComponent)}()},2183:function(e,n,t){"use strict";t.d(n,"a",function(){return d});var s=t(0),o=t(5),a=t(8),l=t(1),i=t(167),r=t(4),c=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},u=this&&this.__metadata||function(e,n){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,n)},d=function(){function AddonMessagesConfirmedContactsComponent(e,n,t,o){var a=this;this.domUtils=e,this.messagesProvider=o,this.onUserSelected=new s.v,this.loaded=!1,this.canLoadMore=!1,this.loadMoreError=!1,this.contacts=[],this.onUserSelected=new s.v,this.memberInfoObserver=n.on(i.a.MEMBER_INFO_CHANGED_EVENT,function(e){if(e.userBlocked||e.userUnblocked){var n=a.contacts.find(function(n){return n.id==e.userId});n&&(n.isblocked=e.userBlocked)}else if(e.contactRemoved){var t=a.contacts.findIndex(function(n){return n.id==e.userId});t>=0&&a.contacts.splice(t,1)}else e.contactRequestConfirmed&&a.refreshData()},t.getCurrentSiteId())}return AddonMessagesConfirmedContactsComponent.prototype.ngOnInit=function(){var e=this;this.fetchData().then(function(){e.contacts.length&&e.selectUser(e.contacts[0].id,!0)}).finally(function(){e.loaded=!0}),this.content.resize()},AddonMessagesConfirmedContactsComponent.prototype.fetchData=function(e){var n=this;void 0===e&&(e=!1),this.loadMoreError=!1;var t=e?0:this.contacts.length;return(0===t?this.messagesProvider.invalidateUserContacts().catch(function(){}):Promise.resolve()).then(function(){return n.messagesProvider.getUserContacts(t)}).then(function(t){n.contacts=e?t.contacts:n.contacts.concat(t.contacts),n.canLoadMore=t.canLoadMore}).catch(function(e){n.loadMoreError=!0,n.domUtils.showErrorModalDefault(e,"addon.messages.errorwhileretrievingcontacts",!0)})},AddonMessagesConfirmedContactsComponent.prototype.refreshData=function(e){return this.fetchData(!0).finally(function(){e&&e.complete()})},AddonMessagesConfirmedContactsComponent.prototype.loadMore=function(e){return this.fetchData().finally(function(){e&&e()})},AddonMessagesConfirmedContactsComponent.prototype.selectUser=function(e,n){void 0===n&&(n=!1),this.selectedUserId=e,this.onUserSelected.emit({userId:e,onInit:n})},AddonMessagesConfirmedContactsComponent.prototype.ngOnDestroy=function(){this.memberInfoObserver&&this.memberInfoObserver.off()},c([Object(s.P)(),u("design:type",Object)],AddonMessagesConfirmedContactsComponent.prototype,"onUserSelected",void 0),c([Object(s._10)(o.f),u("design:type",o.f)],AddonMessagesConfirmedContactsComponent.prototype,"content",void 0),AddonMessagesConfirmedContactsComponent=c([Object(s.m)({selector:"addon-messages-confirmed-contacts",templateUrl:"addon-messages-confirmed-contacts.html"}),u("design:paramtypes",[r.b,a.b,l.b,i.a])],AddonMessagesConfirmedContactsComponent)}()},2184:function(e,n,t){"use strict";t.d(n,"a",function(){return d});var s=t(0),o=t(5),a=t(8),l=t(1),i=t(167),r=t(4),c=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},u=this&&this.__metadata||function(e,n){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,n)},d=function(){function AddonMessagesContactRequestsComponent(e,n,t,o){var a=this;this.domUtils=e,this.messagesProvider=o,this.onUserSelected=new s.v,this.loaded=!1,this.canLoadMore=!1,this.loadMoreError=!1,this.requests=[],this.memberInfoObserver=n.on(i.a.MEMBER_INFO_CHANGED_EVENT,function(e){if(e.contactRequestConfirmed||e.contactRequestDeclined){var n=a.requests.findIndex(function(n){return n.id==e.userId});n>=0&&a.requests.splice(n,1)}},t.getCurrentSiteId())}return AddonMessagesContactRequestsComponent.prototype.ngOnInit=function(){var e=this;this.fetchData().then(function(){e.requests.length&&e.selectUser(e.requests[0].id,!0)}).finally(function(){e.loaded=!0}),this.content.resize()},AddonMessagesContactRequestsComponent.prototype.fetchData=function(e){var n=this;void 0===e&&(e=!1),this.loadMoreError=!1;var t=e?0:this.requests.length;return(0===t?this.messagesProvider.invalidateContactRequestsCache().catch(function(){}):Promise.resolve()).then(function(){return n.messagesProvider.getContactRequests(t)}).then(function(t){n.requests=e?t.requests:n.requests.concat(t.requests),n.canLoadMore=t.canLoadMore}).catch(function(e){n.loadMoreError=!0,n.domUtils.showErrorModalDefault(e,"addon.messages.errorwhileretrievingcontacts",!0)})},AddonMessagesContactRequestsComponent.prototype.refreshData=function(e){return this.messagesProvider.refreshContactRequestsCount(),this.fetchData(!0).finally(function(){e&&e.complete()})},AddonMessagesContactRequestsComponent.prototype.loadMore=function(e){return this.fetchData().finally(function(){e&&e()})},AddonMessagesContactRequestsComponent.prototype.selectUser=function(e,n){void 0===n&&(n=!1),this.selectedUserId=e,this.onUserSelected.emit({userId:e,onInit:n})},AddonMessagesContactRequestsComponent.prototype.ngOnDestroy=function(){this.memberInfoObserver&&this.memberInfoObserver.off()},c([Object(s.P)(),u("design:type",Object)],AddonMessagesContactRequestsComponent.prototype,"onUserSelected",void 0),c([Object(s._10)(o.f),u("design:type",o.f)],AddonMessagesContactRequestsComponent.prototype,"content",void 0),AddonMessagesContactRequestsComponent=c([Object(s.m)({selector:"addon-messages-contact-requests",templateUrl:"addon-messages-contact-requests.html"}),u("design:paramtypes",[r.b,a.b,l.b,i.a])],AddonMessagesContactRequestsComponent)}()},2185:function(e,n,t){"use strict";t.d(n,"a",function(){return h});var s=t(0),o=t(5),a=t(3),l=t(1),i=t(167),r=t(4),c=t(12),u=t(8),d=this&&this.__decorate||function(e,n,t,s){var o,a=arguments.length,l=a<3?n:null===s?s=Object.getOwnPropertyDescriptor(n,t):s;if("object"==typeof Reflect&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,n,t,s);else for(var i=e.length-1;i>=0;i--)(o=e[i])&&(l=(a<3?o(l):a>3?o(n,t,l):o(n,t))||l);return a>3&&l&&Object.defineProperty(n,t,l),l},f=this&&this.__metadata||function(e,n){if("object"==typeof Reflect&&"function"==typeof Reflect.metadata)return Reflect.metadata(e,n)},h=function(){function AddonMessagesContactsComponent(e,n,t,s,o,a,l){var r=this;this.appProvider=t,this.messagesProvider=s,this.domUtils=o,this.eventsProvider=l,this.noSearchTypes=["online","offline","blocked","strangers"],this.loaded=!1,this.contactTypes=this.noSearchTypes,this.searchType="search",this.loadingMessage="",this.hasContacts=!1,this.contacts={online:[],offline:[],strangers:[],search:[]},this.searchString="",this.currentUserId=e.getCurrentSiteUserId(),this.siteId=e.getCurrentSiteId(),this.searchingMessages=n.instant("core.searching"),this.loadingMessages=n.instant("core.loading"),this.loadingMessage=this.loadingMessages,this.discussionUserId=a.get("discussionUserId")||!1,this.memberInfoObserver=l.on(i.a.MEMBER_INFO_CHANGED_EVENT,function(e){e.contactRequestConfirmed&&r.refreshData()},e.getCurrentSiteId())}return AddonMessagesContactsComponent.prototype.ngOnInit=function(){var e=this;this.discussionUserId&&this.gotoDiscussion(this.discussionUserId),this.fetchData().then(function(){if(!e.discussionUserId&&e.hasContacts){var n=void 0;for(var t in e.contacts)if(e.contacts[t].length>0){n=e.contacts[t][0];break}n&&e.gotoDiscussion(n.id,!0)}}).finally(function(){e.loaded=!0})},AddonMessagesContactsComponent.prototype.refreshData=function(e){var n=this;return(this.searchString?this.performSearch(this.searchString):this.messagesProvider.invalidateAllContactsCache(this.currentUserId).then(function(){return n.fetchData()})).finally(function(){e.complete()})},AddonMessagesContactsComponent.prototype.fetchData=function(){var e=this;return this.loadingMessage=this.loadingMessages,this.messagesProvider.getAllContacts().then(function(n){for(var t in n)e.contacts[t]=n[t].length>0?e.sortUsers(n[t]):[];e.clearSearch()}).catch(function(n){e.domUtils.showErrorModalDefault(n,"addon.messages.errorwhileretrievingcontacts",!0)})},AddonMessagesContactsComponent.prototype.sortUsers=function(e){return e.sort(function(e,n){var t=e.fullname.toLowerCase(),s=n.fullname.toLowerCase();return t.localeCompare(s)})},AddonMessagesContactsComponent.prototype.clearSearch=function(){this.searchString="",this.contactTypes=this.noSearchTypes,this.hasContacts=!1;for(var e in this.contacts)if(this.contacts[e].length>0)return void(this.hasContacts=!0)},AddonMessagesContactsComponent.prototype.search=function(e){var n=this;return this.appProvider.closeKeyboard(),this.loaded=!1,this.loadingMessage=this.searchingMessages,this.performSearch(e).finally(function(){n.loaded=!0})},AddonMessagesContactsComponent.prototype.performSearch=function(e){var n=this;return this.messagesProvider.searchContacts(e).then(function(t){n.hasContacts=t.length>0,n.searchString=e,n.contactTypes=["search"],n.contacts.search=n.sortUsers(t)}).catch(function(e){n.domUtils.showErrorModalDefault(e,"addon.messages.errorwhileretrievingcontacts",!0)})},AddonMessagesContactsComponent.prototype.gotoDiscussion=function(e,n){void 0===n&&(n=!1),this.discussionUserId=e;this.eventsProvider.trigger(i.a.SPLIT_VIEW_LOAD_EVENT,{discussion:e,onlyWithSplitView:n},this.siteId)},AddonMessagesContactsComponent.prototype.ngOnDestroy=function(){this.memberInfoObserver&&this.memberInfoObserver.off()},AddonMessagesContactsComponent=d([Object(s.m)({selector:"addon-messages-contacts",templateUrl:"addon-messages-contacts.html"}),f("design:paramtypes",[l.b,a.c,c.b,i.a,r.b,o.t,u.b])],AddonMessagesContactsComponent)}()}});