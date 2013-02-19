function Q_Learner(stateTypes, actionTypes) {
	if (typeof stateTypes != 'object' || typeof actionTypes != 'object') throw "Type Error.";
	
	this.zero = function(array) {
		for (var i = 0; i < array.length; i++) {
			array[i] = 0;
		}
		return array;
	}
	this.length = function(object) {
		var i = 0;
		for (var j in object) {
			if (object.hasOwnProperty(j)) i++;
		}
		return i;
	}
	this.createJoinedArray = function(state, action, reward) {
		var extra = 1;
		if (reward == undefined) {
			extra = 0;
		}
		var joined = new Array(this.states.length + this.actions.length + extra);
		var i = 0;
		for (var j in state) { // these two for loop join action and state values into one array
			if (!state.hasOwnProperty(j)) continue;
			joined[i++] = state[j];				
		}
		for (var j in action) {
			if (!action.hasOwnProperty(j)) continue;
			joined[i++] = action[j];
		}
		if (extra == 1) joined[i++] = reward;
		return joined;
	}
	
	
	this.states = stateTypes;
	this.actions = actionTypes;
	this.mode = "learn";
	this.qEstimator = new kNN_Learner( [this.zero(new Array(stateTypes.length + actionTypes.length + 1))] ); // send blank data value of correct length to kNN estimator
	this.qEstimator.setType("continuous");
	this.learningRate = 0.2;
	this.discountFactor = 0.25;
	this.qEstimator.maxDistance = 10;
	this.defaultQ = 0;
	this.accuracy = 0.9;
	this.minValues = this.zero(new Array(stateTypes.length + actionTypes.length + 1));
	this.maxValues = this.zero(new Array(stateTypes.length + actionTypes.length + 1));

	this.hasStarted = false;
	
	this.requestAction = function(state) {
		if (typeof state != 'object') throw "Type Error.";
		if (this.length(state) != this.states.length) throw "Mismatch Error.";
		
		var action = new Object();
		
		var explore = this.learningRate;
		if (this.mode == "use") {
			explore = 0;
		} else if (this.mode == "learn_1") throw "Mode Error.";
		
		if (Math.random() < explore) {
			for (var i = 0; i < this.actions.length; i++) {
				var range = (this.maxValues[i + this.states.length] - this.minValues[i + this.states.length]);
				var min = this.minValues[i + this.states.length];
				action[this.actions[i]] = min + range * Math.random(); // generate random action value;
			}
		} else {
			var minValues = this.minValues.slice(this.states.length, this.states.length + this.actions.length);
			var maxValues = this.maxValues.slice(this.states.length, this.states.length + this.actions.length);
			var accuracy = Math.ceil(1 / (1 - this.accuracy));
			for (var k = 0; k < accuracy; k++) {
				var test = new Array(this.actions.length * 2 + 2);
				for (var i = 0; i < test.length; i++) {
					test[i] = new Array(this.actions.length);
				}
				for (var i = 0; i < this.actions.length; i	++) {
					// three parabola points
					var min = minValues[i];
					var max = maxValues[i];
					var q1 = 0.25 * (max + min);
					var q3 = 3 * q1;
					for (var j = 0; j < test.length / 2; j++) {				
						if (Math.floor(j - 1) == i) { 
							test[j][i] = q3; 
						} else {
							test[j][i] = q1;
						}
					}
					for (var j = 0; j < test.length / 2; j++) {				
						if (Math.floor(j - 1) == i) { 
							test[j + test.length / 2][i] = q1; 
						} else {
							test[j + test.length / 2][i] = q3;
						}
					}
				}
				var joinedState = new Array(this.states.length);
				var i = 0;
				for (var j in state) { // these two for loop join action and state values into one array
					if (!state.hasOwnProperty(j)) continue;
					joinedState[i++] = state[j];				
				}
				var results = [];
				for (var i = 0; i < test.length; i++) {
					results[i] = this.qEstimator.query(joinedState.concat(test[i]), this.defaultQ);
				}
				for (var i = 0; i < this.actions.length; i++) {
					var q3 = 0.5 * (results[1 + i] + results[this.actions.length + 1]);
					var q1 = 0.5 * (results[0] + results[this.actions.length + 2 + i]);
					if (q3 > q1) {
						minValues[i] = 0.5 * (minValues[i] + maxValues[i]);
					} else {
						maxValues[i] = 0.5 * (minValues[i] + maxValues[i]);
					}
				}
			}
			for (var i = 0; i < this.actions.length; i++) {
				action[this.actions[i]] = 0.5 * (test[this.actions.length + 1][i] + test[0][i]);
			}
		}
		return action;
	}
	
	this.updateQFactors = function(state, action, reward, newstate) {
		var q = reward;
		var joined = this.createJoinedArray(state, action, q);
		
		var sum = 0;
		for (var i = 0; i < joined.length; i++) { // update min/max values
			this.maxValues[i] = (joined[i] > this.maxValues[i]) ? joined[i] : this.maxValues[i];
			this.minValues[i] = (joined[i] < this.minValues[i]) ? joined[i] : this.minValues[i];
			sum += this.maxValues[i] - this.minValues[i];
		}
		this.setBandwidth(sum / joined.length / ((this.states.length + this.actions.length) / 2));  // set bandwidth as a percent of average range values
		
		if (this.hasStarted) {	 // add state + action + reward vector to Q-value estimator
			this.qEstimator.addPoint(joined);
		} else {
			this.hasStarted = true;
			this.qEstimator.clear([joined]);
		}
	}
	
	this.setAction = function(state, action) {
		if (typeof state != 'object') throw "Type Error.";
		if (typeof action != 'object') throw "Type Error.";
		if (this.length(state) != this.states.length) throw "Mismatch Error.";
		if (this.length(action) != this.actions.length) throw "Mismatch Error.";
		if (this.mode != "learn_1") throw "Mode Error.";
	}
	
	this.setMode = function(mode) {
		if (mode == "learn_1" || mode == "learn_2" || mode == "use") {
			this.mode = mode;
		} else {
			throw "Type Error.";
		}
	}
	
	this.setLearningRate = function(rate) {
		if (rate > 0 && rate < 1) {
			this.learningRate = rate;
		} else {
			throw "Type Error.";
		}
	}
	
	this.setMinimumValues = function(minValues) {
		if (typeof minValues != 'object') throw "Type Error.";
		this.minValues = minValues;
	}
	
	this.setMaximumValues = function(maxValues) {
		if (typeof maxValues != 'object') throw "Type Error.";
		this.maxValues = maxValues;
	}
	
	this.setRanges = function(minValues, maxValues) {
		if (typeof maxValues != 'object') throw "Type Error.";
		if (typeof minValues != 'object') throw "Type Error.";
		this.minValues = minValues;
		this.maxValues = maxValues;
	}
	
	this.setK = function(k) {
		if (typeof k != 'number') throw "Type Error.";
		this.qEstimator.setK(k);
	}
	
	this.setDefaultQ = function(q) {
		if (typeof q != 'number') throw "Type Error.";
		this.defaultQ = q;
	}
	
	this.setBandwidth = function(bandwidth) {
		if (typeof bandwidth != 'number') throw "Type Error.";
		this.qEstimator.maxDistance = bandwidth;
	}
	
	this.setAccuracy = function(accuracy) {
		if (typeof accuracy != 'number') throw "Type Error.";
		if (0 >= accuracy || accuracy >= 1) throw "Range Error.";
		this.accuracy = accuracy;
	}
	
	this.setDiscountFactor = function(factor) {
		if (factor > 0 && factor < 1) {
			this.discountFactor = factor;
		} else {
			throw "Type Error.";
		}
	}
	
	this.loadData = function(data) {
		if (typeof data != 'object') throw "Type Error.";
		this.qEstimator.data = data;
	}
}