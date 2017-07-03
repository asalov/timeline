// Timeline info
var wc = {
	start: 1930,
	end: 2030,
	default:2014,
	chosen:2014
};

$(document).ready(function(){
	'use strict';

	setTimeout(function(){
		$('.util').addClass('hide');
	}, 10);

	var allYears = [];

	var $timelineBar = $('.progress-bar');
	var $events = $('.event');
	var $backToTop = $('.back-to-top-btn');
	var $timelineWidth = 2400;

	// Initialize double slider
	var $slider = $('#filterSlider');

	if($slider.length > 0){
		var $filterSlider = new Foundation.Slider($('#filterSlider'), {
			step: 4
		});
	}

	var optionalInput = {
		pics: 'picture-upload', 
		stats: 'statistics', 
		facts: 'facts',
		utility: 'util'
	};

	// Initialize confirmation dialog
	var $deleteDialog = $('#deleteConfirmation');
	var deleteConfirm = new Foundation.Reveal($deleteDialog, {
		closeOnClick: false
	});

	// Toggle menu icon
	$('.toggle-icon').click(function(){
		$(this).find('i').toggleClass('fa-bars fa-times');
	});

	$('#year-val').text($('#year').val());

	// Change slider value
	$('#year, #filterSlider').on('mouseup mousedown mouseout mousemove', function(e){

		var $this = $(this);
		var $name = $(this).attr('id');

		if($name === 'year'){
			var $year = $this.val();

			$('#' + $name + '-val').text($year);			
		}else{
			var $yearMin = $this.find('#year-min');
			var $yearMax = $this.find('#year-max');
			var start = wc.start + num($yearMin.val());
			var end = wc.start + num($yearMax.val());

			$('#' + $yearMin.attr('id') + '-val').text(start);
			$('#' + $yearMax.attr('id') + '-val').text(end);

			if(e.type !== 'mousemove') filterEntries(start, end);
		}
	});

	// Show confirmation dialog
	$('#deleteEntry').click(function(e){
		e.preventDefault();

		deleteConfirm.open();
	});

	// Close delete dialog
	$deleteDialog.find('.close-button').add('#cancelDelete').click(function(){
		deleteConfirm.close();
	});

	// Perform deletion
	$('#confirmDelete').click(function(){
		var $url = $('#deleteEntry').attr('action');
		
		var options = {
			method: 'DELETE'
		};

		ajax($url, function(res){
			if(!res.error) location.href = '../';
		}, options);
	});

	// Position elements on timeline
	$events.each(function(){
		var $this = $(this);
		var $year = num($this.find('.year').text());
		var $url = $this.find('a').attr('href');

		allYears.push($year);

		var pos = calcTimelinePos($timelineWidth, $year, 370);
		$this.css('left', pos + 'px');

		if(wc.chosen > $year){
			$this.addClass('passed');
		}else if(wc.chosen === $year){
			$this.addClass('current');
		}

		if($year === wc.default){
			scrollToPos(calcTimelinePos($timelineWidth, $year, 100), 'start');
			
			$('.progress-meter').css('width', calcTimelineProgress($year) + '%');
			
			showContent($url);
			moveTimeline($timelineWidth);
		}
	});

	// Navigate through timeline
	$('.arrow').click(function(){
		var $this = $(this);

		if($this.hasClass('arrow-left') && wc.chosen > wc.start){
			var prev = findItem(wc.chosen, allYears, false);

			navigateToYear($timelineWidth, prev);
		}else if($this.hasClass('arrow-right') && wc.chosen < wc.end){
			var next = findItem(wc.chosen, allYears, true);

			navigateToYear($timelineWidth, next);
		}
	});

	// Set timeline progress
	$events.click(function(e){
		e.preventDefault();

		var $this = $(this);
		var $year = num($this.find('.year').text());

		navigateToYear($timelineWidth, $year);
	});

	// Update timeline points after navigating to chosen year
	$('.progress-meter').bind('transitionend', function(){
		$events.each(function(){
			var $this = $(this);
			var $year = num($this.find('.year').text());

			if(wc.chosen > $year){
				$this.addClass('passed');
				$this.removeClass('current');
			}else if(wc.chosen < $year){
				$this.removeClass('passed');
				$this.removeClass('current');
			}else if(wc.chosen === $year){
				$this.addClass('current');
			}
		});
	});

	// Create event handlers for each switch
	for(var x in optionalInput){
		var inputName = x.charAt(0).toUpperCase() + x.slice(1);

		$('#toggle' + inputName).change(toggleElem(optionalInput[x]));
	}

	// Back to top
	$backToTop.click(function(){
		$('html').animate({ scrollTop:0 }, 750);
	});

	// Toggle back to top button
	$(window).scroll(function(){
		var $scrollPos = $(this).scrollTop();

		($scrollPos > 200) ? $backToTop.removeClass('hide') : $backToTop.addClass('hide');
	});

	$(window).resize(function(){
		moveTimeline($timelineWidth);
	});

	// Sort entries
	$('#sortEntries').change(function(){
		var $entries = $('.entry');
		var $chosen = $(this).val();
		var sortFunc = null;

		switch($chosen){
			case 'year-desc':
				sortFunc = function(a, b){
					var $aYear = $(a).find('.year').text().replace(/World Cup/g, '');
					var $bYear = $(b).find('.year').text().replace(/World Cup/g, '');
					
					return $aYear < $bYear;
				};
			break;
			case 'creation-asc':
				sortFunc = function(a, b){
					var $aCreation = new Date($(a).find('.creation-date').val());
					var $bCreation = new Date($(b).find('.creation-date').val());

					return $aCreation > $bCreation;
				};			
			break;			
			case 'creation-desc':
				sortFunc = function(a, b){
					var $aCreation = new Date($(a).find('.creation-date').val());
					var $bCreation = new Date($(b).find('.creation-date').val());
					
					return $aCreation < $bCreation;
				};				
			break;
			case 'last-modified':
				sortFunc = function(a, b){
					var $aModified = new Date($(a).find('.last-modified').val());
					var $bModified = new Date($(b).find('.last-modified').val());
					
					return $aModified < $bModified;
				};				
			break;
			default:
				sortFunc = function(a, b){
					var $aYear = $(a).find('.year').text().replace(/World Cup/g, '');
					var $bYear = $(b).find('.year').text().replace(/World Cup/g, '');
					
					return $aYear > $bYear;
				};			
			break;
		}

		$entries.sort(sortFunc).appendTo('.entries');
	});

	// Exclude empty fields from being submitted
	$('.entry-form').submit(function(){
		var $this = $(this);

		var $elements = $this.find(':input');

		$elements.each(function(){
			var $elem = $(this);

			if($elem.val().trim() === '') $elem.removeAttr('name');
		});
	});

	// Show which files have been selected
	$('#photos').change(function(){
		var $this = $(this);
		var $files = $this[0].files;
		var $numFiles = $files.length;
		var msg = '';

		for(var i = 0; i < $numFiles; i++){
			msg += $files[i].name;

			if(i < $files.length - 1) msg += ', ';
		}

		msg += ' selected (' + $numFiles + ' file';
		msg += ($numFiles > 1) ? 's ' : ' ';
		msg += 'total).';

		$('.selected-files').text(msg);
	});

	// Dismiss confirmation message
	$('.success-msg .close-button').click(function(){
		$(this).parent().fadeOut(250);
	});

	// Add new input field for stats
	$('#eventStats').change(function(){
		var $this = $(this);

		var $selected = $this.children('option:selected');

		if($selected.val().trim() !== ''){
			$selected.prop('disabled', true);

			var $label = $('<label/>', {text: $selected.text()});
			var $hidden = $('<input/>', {type:'hidden', name:'statValues', value:$selected.val()});
			var $input = $('<input/>', {type:'text', class:'stat-field'});

			$this.parents('.statistics').append([$label, $hidden, $input]);
		}
	});

	// Append stat value to hidden field
	$('.statistics').on('blur', '.stat-field', function(){
		var $this = $(this);

		$this.prev().val($this.prev().val() + '~' + $this.val());
	});

	// Add a new input field for facts
	$('.add-fact').click(function(e){
		e.preventDefault();

		var $this = $(this);
		var $numFacts = $this.parent().find('.fact').length;

		var $factDiv = $('<div/>', { class: 'fact'});
		var $label = $('<label/>', { text: 'Fact ' + ($numFacts + 1)});
		var $input = $('<input/>', { type:'text', name: 'facts'});

		$factDiv.append([$label, $input]);

		$this.before($factDiv);
	});

	$('.pic .close-button').click(function(){
		var $this = $(this);
		var $picId = $this.parent().attr('data-pic-id');
		var $picDiv = $this.parents('.entry-pics');

		removeItem('pictures', $picId, function(){
			$this.parent().remove();

			if($picDiv.find('.pic').length === 0) $picDiv.remove();
		});
	});

	$('.entry-stats .close-button').click(function(){
		var $this = $(this);
		var $statId = $this.parents('tr').attr('data-stat-id');
		var $statDiv = $this.parents('.entry-stats');
		var $statName = $this.parent().prev().text();

		removeItem('stats', $statId, function(){
			$this.parents('tr').remove();

			// Enable option again
			$('#eventStats').find('option:contains(' + $statName + ')').prop('disabled', false);

			if($statDiv.find('tr').length === 0) $statDiv.remove();
		});
	});

	$('.entry-facts .close-button').click(function(){
		var $this = $(this);
		var $factId = $this.parent().attr('data-fact-id');
		var $factDiv = $this.parents('.entry-facts');

		removeItem('facts', $factId, function(){
			$this.parents('tr').remove();

			if($factDiv.find('tr').length === 0) $factDiv.remove();
		});
	});

	$('#readMore').click(function(e){
		e.preventDefault();

		$('.more-info').removeClass('hide');
		$(this).addClass('hide');
	});

}); // END ready

// Send an AJAX request
function ajax(url, callback, options){
	// Defaults
	var method = 'GET';
	var data = {};

	if(options !== undefined){
		method = options.method || method;
		data = options.data || data;
	}

	$.ajax({
		url: url,
		method: method,
		data: data,
		dataType: 'json'
	}).done(callback);
}

// Calculate progress value based on selected year
function calcTimelineProgress(year){
	return year - wc.start;
}

// Calculate the position of events on timeline
function calcTimelinePos(timelineWidth, year, adjustment){
	var adjust = adjustment || 0;

	var percent = calcTimelineProgress(year);

	return getValueFromPercentage(timelineWidth, percent) + adjust;
}

// Turn percentage into number
function getValueFromPercentage(total, percent){
	return (percent / 100) * total;
}

// Navigate timeline to chosen year
function navigateToYear(timelineWidth, year){
	$('#readMore').addClass('hide');
	$('.more-info').addClass('hide');

	$('.chosen-entry').fadeTo(10, 0);

	var $timelineProgress = $('.progress-meter');

	scrollToPos(calcTimelinePos(timelineWidth, year, 100));
	$timelineProgress.css('width', calcTimelineProgress(year) + '%');

	wc.chosen = year;

	showContent(findItemId(year));
}

// Scroll timeline to selected position
function scrollToPos(pos, section){
	var $bar = $('.bar-container');

	if(section === 'start'){
		$bar.scrollLeft(pos);
	}else{
		$bar.animate({
		    scrollLeft: pos
		}, 1000);		
	}
}

// Find item value in year array
function findItem(year, arr, next){
	var index = arr.indexOf(year);

	if(index > -1) return next ? arr[index + 1] : arr[index - 1];

	return year;
}

function findItemId(year){
	return $('.event .year:contains("' + year + '")').parent().attr('href');
}

// Convert string to number
function num(val){
	return Number(val);
}

// Show the content for the chosen entry
function showContent(url){
	ajax(url, function(res){
		if(!res.error){
			var $chosen = $('.chosen-entry');
			var $featuredImg = $chosen.find('.thumbnail img');
			var $summary = $chosen.find('.summary');
			var $recap = $chosen.find('.recap');
			var $extraPics = $chosen.find('.more-pics');
			var $stats = $chosen.find('.stats');
			var $facts = $chosen.find('.facts');
			var i = 0;

			$chosen.find('.year').text(res.year + ' FIFA World Cup');
			$featuredImg.attr('alt', res.year + ' FIFA World Cup');

			var imgSrc = 'uploads/default.jpg';

			if(res.pictures.length > 0) imgSrc = res.pictures[0].url;

			$featuredImg.attr('src', imgSrc);
			$chosen.find('.summary').text(res.summary);

			if(res.tournamentRecap !== null || res.pictures.length > 1 || 
				res.stats.length > 0 || res.facts.length > 0){
				$('#readMore').removeClass('hide');

				if(res.tournamentRecap !== null){
					$recap.removeClass('hide');
					$recap.find('p').text(res.tournamentRecap);
				}else{
					$recap.addClass('hide');
				}

				if(res.pictures.length > 1){
					$extraPics.removeClass('hide');
					var $picContainer = $extraPics.find('.event-pic-container');
					$picContainer.empty();

					for(i = 1; i < res.pictures.length; i++){
						var $pic = $('<div/>', { class: 'event-pic column small-12 medium-6 large-4'});
						$pic.css('background-image', 'url("' + res.pictures[i].url + '")');

						$picContainer.append($pic);
					}
				}else{
					$extraPics.addClass('hide');
				}

				if(res.stats.length > 0){
					$stats.removeClass('hide');
					var $statTable = $stats.find('table');
					$statTable.empty();

					for(i = 0; i < res.stats.length; i++){
						var $statTr = $('<tr/>');
						var $statName = $('<td/>', {text: res.stats[i].stat.name});
						var $statVal = $('<td/>', {text: res.stats[i].value});

						$statTr.append([$statName, $statVal]);
						$statTable.append($statTr);
					}

				}else{
					$stats.addClass('hide');
				}

				if(res.facts.length > 0){
					$facts.removeClass('hide');
					var $factTable = $facts.find('table');
					$factTable.empty();

					for(i = 0; i < res.facts.length; i++){
						var $factTr = $('<tr/>');
						var $td = $('<td/>', {text: res.facts[i].desc});

						$factTr.append($td);
						$factTable.append($factTr);
					}
				}else{
					$facts.addClass('hide');
				}
			}

			$chosen.fadeTo(1000, 1);
		}
	});
}

// Toggle visibility of optional inputs
function toggleElem(elemName){
	return function(){
		$('.optional-input.' + elemName).toggleClass('hide');
	};
}

function filterEntries(start, end){
	var $entries = $('.entry');
	var $noResults = $('.no-results');

	$entries.each(function(){
		var $this = $(this);
		var $year = num($this.find('.year').text().substr(0, 4));

		($year < start || $year > end) ? $this.addClass('hide') : $this.removeClass('hide');

		($('.entry:visible').length === 0) ? $noResults.removeClass('hide') : $noResults.addClass('hide');
	});
}

// Check if a child element exists
function elemExists(parent, selector){
	return parent.find(selector).length > 0;
}

function moveTimeline(timelineWidth){
	var $timelineBar = $('.progress-bar');
	var $screenWidth = $(window).width();
	var pos;

	switch(true){
		case ($screenWidth <= 320):
			pos = -160;
		break;
		case ($screenWidth <= 360):
			pos = -140;
		break;
		case ($screenWidth <= 480):
			pos = -70;
		break;
		case ($screenWidth <= 640):
			pos = 0;
		break;
		case ($screenWidth <= 768):
			pos = 70;			
		break;										
		default:
			pos = 100;
		break;
	}

	$timelineBar.css('left', pos + 'px');
	scrollToPos(calcTimelinePos(timelineWidth, wc.chosen, 100));
}

// Remove entry item (picture, stat, fact)
function removeItem(type, id, callback){
	var $entryId = $('#entryId').val();
	var host = location.host;
	var url = '';

	url += (host.indexOf('localhost') > -1) ? 'http://' + host : host;
	url += '/entries/' + $entryId + '/' + type + '/' + id;

	var options = {
		method: 'DELETE'
	};

	ajax(url, callback, options);
}