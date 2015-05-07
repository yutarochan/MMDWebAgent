$(document).ready(function() {
    var canvas = document.createElement('canvas');
    canvas.width = screen.width;
    canvas.height = screen.height;
    // canvas.style.border = 'solid black 1px'; // NO BORDER BRO

    document.body.appendChild(canvas);

    var mmd = new MMD(canvas, canvas.width, canvas.height);
    mmd.initShaders();
    mmd.initParameters();
    mmd.registerKeyListener(document);
    mmd.registerMouseListener(document);

    console.log(mmd.isPlaying);

    var miku = new MMD.Model('model', 'mei.pmd');
    miku.load(function() {
        mmd.addModel(miku);
        mmd.initBuffers();
        mmd.start();

        var prev_response;

        var dance = new MMD.Motion('motion/mei_wait.vmd');
        dance.load(function() {
            mmd.addModelMotion(miku, dance, true);
            mmd.play();
            mmd.rewind();

            tts_talk("Hi, I'm Wanda. I'm a chatter bot entity on your very own browser.");

            // Microphone Modules
            var select_language = 'English';
            var select_dialect = ['en-US', 'United States'];
            select_language.selectedIndex = 6;
            select_dialect.selectedIndex = 6;

            function updateCountry() {
                for (var i = select_dialect.options.length - 1; i >= 0; i--) {
                    select_dialect.remove(i);
                }
                var list = langs[select_language.selectedIndex];
                for (var i = 1; i < list.length; i++) {
                    select_dialect.options.add(new Option(list[i][1], list[i][0]));
                }
                select_dialect.style.visibility = list[1].length == 1 ? 'hidden' : 'visible';
            }

            var create_email = false;
            var final_transcript = '';
            var recognizing = false;
            var ignore_onend;
            var start_timestamp;
            if (!('webkitSpeechRecognition' in window)) {
                upgrade();
            } else {
                var recognition = new webkitSpeechRecognition();
                recognition.continuous = true;
                recognition.interimResults = true;

                recognition.start();

                recognition.onstart = function() {
                    recognizing = true;
                };

                recognition.onerror = function(event) {
                	console.log("dem errors");
                    if (event.error == 'no-speech') {
                        ignore_onend = true;
                    }
                    if (event.error == 'audio-capture') {
                        ignore_onend = true;
                    }
                    if (event.error == 'not-allowed') {
                        ignore_onend = true;
                    }
                };

                recognition.onresult = function(event) {
                    var interim_transcript = '';
                    if (typeof(event.results) == 'undefined') {
                        recognition.onend = null;
                        upgrade();
                        return;
                    }
                    for (var i = event.resultIndex; i < event.results.length; ++i) {
                        if (event.results[i].isFinal) {
                            final_transcript += event.results[i][0].transcript;
                        } else {
                            interim_transcript += event.results[i][0].transcript;
                        }
                    }

                    //////////////////////

                    final_transcript = capitalize(final_transcript);
                    console.log(final_transcript);
                    // TOOD: Send API REQUEST HERE...

                    if (final_transcript !== prev_response && final_transcript !== "") {
	                	$.ajax({url:"http://21fc170c.ngrok.com/"+final_transcript,
	                		dataType: "html",
	                    	success:function(result){
	                    		//console.log("I got something");
	                    		//console.log(result);
	    						tts_talk(result);
	  						},
	  						error: function (xhr, ajaxOptions, thrownError) {
	        					console.log(xhr.status);
	        					console.log(thrownError);
	      					}

	  					});
	                }

                    final_transcript = '';

                    ///////////////////////
                };
            }

            var two_line = /\n\n/g;
            var one_line = /\n/g;

            function linebreak(s) {
                return s.replace(two_line, '<p></p>').replace(one_line, '<br>');
            }

            var first_char = /\S/;

            function capitalize(s) {
                return s.replace(first_char, function(m) {
                    return m.toUpperCase();
                });
            }

            function startButton(event) {
                if (recognizing) {
                	console.log("stopping...")
                    recognition.stop();
                    return;
                }
                final_transcript = '';
                recognition.lang = select_dialect.value;
                recognition.start();
                ignore_onend = false;
                final_span.innerHTML = '';
                interim_span.innerHTML = '';
                showInfo('info_allow');
                start_timestamp = event.timeStamp;
            }

            var current_style;
        });
    });
});-l

function tts_talk(data) {
    var u = new SpeechSynthesisUtterance(data);
    speechSynthesis.speak(u);
}