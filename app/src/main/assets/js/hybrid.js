;(function () {
    var Dispatcher = {
        callbacks: {},

        send: function(data, complete) {
            this.dispatchAction("event", data, complete);
        },

        sendCallback: function(data) {
            this.dispatchAction("callback", data, function() {});
        },

        triggerCallback: function(id) {
            var dispatcher = this;

            var args = Array.prototype.slice.call(arguments);
            args.shift();

            setTimeout(function() {
                var callback = dispatcher.callbacks[id];
                callback.apply(this, args);
            }, 0);
        },

        dispatchAction: function(action, data, complete) {
            var dispatcher = this;
            this.callbacks[data.id] = function() {
                complete.apply(this, arguments);
                delete dispatcher.callbacks[data.id];
            };
	        var src = "hybrid://" + action + "?" + encodeURIComponent(JSON.stringify(data));
            var iframe = document.createElement("iframe");
            iframe.setAttribute("src", src); 
            document.documentElement.appendChild(iframe); 
            iframe.parentNode.removeChild(iframe); 
            iframe = null; 
	  }
	};   

    var Hybrid = {
        listeners: {},

        dispatcher: null,

        actionIndex: 0,

        on: function(type, fn) {
            if (!this.listeners.hasOwnProperty(type) || !this.listeners[type] instanceof Array) {
                this.listeners[type] = [];
            }

            this.listeners[type].push(fn);
        },

        off: function(type) {
            if (!this.listeners.hasOwnProperty(type) || !this.listeners[type] instanceof Array) {
                this.listeners[type] = [];
            }

            this.listeners[type] = [];
        },

        send: function(type, entity, complete) {
            if (entity instanceof Function) {
                complete = entity;
                entity = null;
            }

            entity = entity || {};
            complete = complete || function() {};

            var data = this.createData(this.actionIndex, type, entity);

            this.dispatcher.send(data, complete);

            this.actionIndex += 1;
        },

        trigger: function(type, id, json) {
            var self = this;

            var listenerList = this.listeners[type] || [];

            var complete = function(entity) {

                var data = Hybrid.createData(id,type,entity);
                self.dispatcher.sendCallback(data);
            };

            for (var index = 0; index < listenerList.length; index++) {
                var listener = listenerList[index];
                listener(json, complete);
            }

        },

        triggerCallback: function(id) {
            this.dispatcher.triggerCallback.apply(this.dispatcher, arguments);
        },

        createData: function(id, type, entity) {
            return {
                id: id,
                type: type,
                entity: entity
            };
        }
    };

    Hybrid.dispatcher = Dispatcher;

    window.Hybrid = Hybrid;
})();
