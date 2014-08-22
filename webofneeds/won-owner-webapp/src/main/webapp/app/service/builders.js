/*
 * Copyright 2012  Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Created by LEIH-NB on 19.08.2014.
 */

(function(){
    // determine if in-browser or using node.js

    var _browser = (typeof window !== 'undefined' || typeof self !== 'undefined');
    if(_browser) {
        if(typeof global === 'undefined') {
            if(typeof window !== 'undefined') {
                global = window;
            } else if(typeof self !== 'undefined') {
                global = self;
            } else if(typeof $ !== 'undefined') {
                global = $;
            }
        }
    }

// attaches wonmessagebuilder API to the given object
    var wrapper = function(wonmessagebuilder) {
        /*
         * Creates a JSON-LD representation of the need data provided through builder functions.
         * e.g.:
         * jsonLD = new NeedBuilder().title("taxi").description("need a taxi").build();
         */
        wonmessagebuilder.NeedBuilder = function NeedBuilder() {
            this.data =
            {
                "@context": {
                    "@base": "http://www.example.com/resource/need/randomNeedID_1",
                    "webID": "http://www.example.com/webids/",
                    "msg": "http://purl.org/webofneeds/message#",
                    "dc": "http://purl.org/dc/elements/1.1/",
                    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
                    "geo": "http://www.w3.org/2003/01/geo/wgs84_pos#",
                    "xsd": "http://www.w3.org/2001/XMLSchema#",
                    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                    "won": "http://purl.org/webofneeds/model#",
                    "gr": "http://purl.org/goodrelations/v1#",
                    "ldp": "http://www.w3.org/ns/ldp#",
                    "containsMessage": {
                        "@id": "http://purl.org/webofneeds/message#containsMessage",
                        "@type": "@id"
                    }
                },
                "@graph": [
                    {
                        "@graph": [
                            {
                                "@type": "won:Need",
                                "won:hasContent": "_:n01"
                            },
                            {
                                "@id": "_:n01",
                                "@type": "won:NeedContent"
                            }
                        ]
                    }
                ]
            };
        }

        wonmessagebuilder.NeedBuilder.prototype = {
            constructor: wonmessagebuilder.NeedBuilder,

            getContentNode: function () {
                return this.data["@graph"][0]["@graph"][1];
            },
            getMainNode: function () {
                return this.data["@graph"][0]["@graph"][0];
            },
            getContext: function () {
                return this.data["@context"];
            },
            supply: function () {
                return this.basicNeedType("won:Supply");
            },

            demand: function () {
                return this.basicNeedType("won:Demand");
            },
            doTogether: function () {
                return this.basicNeedType("won:DoTogether");
            },
            critique: function () {
                return this.basicNeedType("won:Critique");
            },
            basicNeedType: function (type) {
                this.getMainNode()["won:hasBasicNeedType"] = type;
                return this;
            },

            description: function (description) {
                this.getContentNode()["won:hasTextDescription"] = description;
                return this;
            },
            needURI: function (needURI) {
                this.getContext()["@base"] = needURI;
                this.getMainNode()["@id"] = needURI;
                this.data["@graph"][0]["@id"] = needURI + "/core#data";
                return this;
            },
            title: function (title) {
                this.getContentNode()["dc:title"] = title;
                return this;
            },
            build: function () {
                console.log("built this data:" + JSON.stringify(this.data));
                return this.data;
            }
        }
       // window.NeedBuilder = NeedBuilder;

        wonmessagebuilder.CreateMessageBuilder = function CreateMessageBuilder(dataset) {
            this.data = dataset;
        };

        wonmessagebuilder.CreateMessageBuilder.prototype = {
            constructor: wonmessagebuilder.CreateMessageBuilder,

            addMessageGraph: function () {
                this.data['@graph'][1] =
                {
                    "@id": "urn:x-arq:DefaultGraphNode",
                    "@graph": [
                        {
                            "@id": "_:b0"
                        }
                    ]
                };
                this.data['@graph'][2] = {
                    "@id": ":/event/0#data",
                    "@graph": [
                        {
                            "@id": ":/event/0",
                            "@type": "msg:CreateMessage",
                            "msg:hasContent": [
                                {
                                    "@id": ":/core#data"
                                },
                                {
                                    "@id": ":/transient#data"
                                }
                            ]
                        }
                    ]
                }
                return this;
            },
            eventURI: function (eventId) {
                this.getDefaultGraphNode()['msg:containsMessage'] = this.data["@context"]["@base"] + "/event/" + eventId + "#data";  //TODO: abstract class
                return this;
            },
            getDefaultGraphNode: function () {
                return this.data["@graph"][1]["@graph"][0];
            },
            getMessageEventNode: function () {
                return this.data["@graph"][2]["@graph"][0]
            },
            build: function () {
                console.log("built this message:" + JSON.stringify(this.data));
                return this.data;
            }
        }
      //  window.CreateMessageBuilder = CreateMessageBuilder;

//}
        return wonmessagebuilder;
    };
    var factory = function() {
        return wrapper(function() {
            return factory();
        });
    };
// the shared global wonmessagebuilder API instance
    wrapper(factory);
   if(_browser) {
        // export simple browser API
        if(typeof wonmessagebuilder === 'undefined') {
            wonmessagebuilder = wonmessagebuilderjs = factory;
        } else {
            wonmessagebuilder = factory;
        }
    }
})();


needJson = new window.wonmessagebuilder.NeedBuilder()
    .title("testneed")
    .needURI("http://localhost:8080/won/resource/need/lk234njkhsdjfgb4l25rtj34")
    .description("just a test")
    .supply()
    .build();

messageJson = new window.wonmessagebuilder.CreateMessageBuilder(needJson)
    .addMessageGraph()
    .eventURI("34543242134")
    .build();

var socket;


var connect = function () {

    var url = document.getElementById("targetAddress").value;

    var options = {debug: true};

    socket = new SockJS(url, null, options);

    socket.onopen = function () {
        console.log("connection has been established!")
        writeOutput("connection has been established!");
    }

    socket.onmessage = function (event) {
        console.log("Received data: "+event.data);

        writeOutput('Received data: ' + event.data);
    };

    socket.onclose = function () {
        console.log("Lost connection")
        writeOutput('Lost connection!');
    };
};

var sendMessage = function () {

    var message = document.getElementById("messageText").value;

    socket.send(message);
}

var closeConnection = function () {
    socket.close;
}

var writeOutput = function (output) {
    var outputDIV = document.getElementById("output");
    outputDIV.value = outputDIV.innerHTML + output + "<br />";
}