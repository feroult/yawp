// leadpages_input_data variables come from the template.json "variables" section
var leadpages_input_data = {};

// FLEXSLIDER PLUGIN
$(window).load(function() {

    $('.flexslider').flexslider({
        animation: "slide",
        smoothHeight: true,
        slideshow: true,
        slideshowSpeed: "6000",
        animationSpeed: "1000"        
    });

    // Set the matchMedia with a listener for window changes
    // added for better UI experience when on mobile
    var query = window.matchMedia("(max-width: 767px)");
    query.addListener(mediaChange);


    function mediaChange(query) {
      if(query.matches) {
        //We are 780px or below, pause flexslider animation
        $('.flexslider').flexslider("pause");
      } else {
        //We are above 780px, play flexslider animation
        $('.flexslider').flexslider("play");
      }
    }

});

// CUSTOM JQUERY FUNCTIONALITY
$(function () {

    function updatePageForBgImg(){

        // this is for setting the background image using size cover
        // top background image
        $('.header-bg').css('background-image', 'url('+$("#header-bg-image").attr("src")+')').css('background-size' , 'cover').css('background-position' , 'top center');

    }

    function updatePageForLwrImg() {

        //lower background
        $('.contact-bg').css('background-image', 'url('+$("#contact-bg-image").attr("src")+')').css('background-size' , 'cover').css('background-position' , 'top center');
    }


    // Either run the DOM update functions once for a published page or continuously for within the builder. 
    if ( typeof window.top.App === 'undefined' ) {
        // Published page
        // flexslider class must be on doc ready
        $(document).ready( function() {

            $( '#bannerSlide' ).addClass( 'flexslider' );
            $( '.displayTestOnly' ).addClass( 'hideBox' );

        });
        
        $(window).on('load', function(){

            updatePageForBgImg();
            updatePageForLwrImg();

        });
    } else {
        // within the builder
        setInterval( function(){

            if ( $( '#header-bg-image' ).css( 'display' ) == "none" ) {
                $( '.header-bg' ).css( 'background-image' , 'none' );
            }
            else {
                updatePageForBgImg();
            }
            if ( $( '#contact-bg-image' ).css( 'display' ) == "none" ) {
                $( '.contact-bg' ).css( 'background-image' , 'none' );
            }
            else {
                updatePageForLwrImg();
            }

                // builder specific as published has no variable to process sizing for the body padding
                // builder adds display none to give appearance of not being present
                if( $( '.menu' ).css( 'display' ) == "none" ) {
                    $( 'body' ).css( 'padding-top','0' );
                }
                else {
                    $( 'body' ).css( 'padding-top', headerHeight - 5);
                }

        }, 500);
    }




// variable header height for smooth scroll fixed header
var headerHeight = $( '.menu' ).outerHeight(); // set the global variable

    $(window).on("load resize", function() { // reset variable on resize and load, load is for safari as it must be set once load is complete
        headerHeight = $( '.menu' ).outerHeight();

        // this sets the padding-top of the body minus 5 pixels
        $( 'body' ).css( 'padding-top', headerHeight - 5);

    }); // window.on resize

    // function for smooth scroll to id links within the page
    $('a[href*=#]:not([href=#])').click(function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
          var target = $(this.hash);
          target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
          if (target.length) {
            $('html,body').animate({
              scrollTop: target.offset().top - headerHeight
            }, 1000);
            return false;
          }
        }
    });





    // menu function
    $( '.mobileMenu' ).click( function() {
        $( '.menu__content__options' ).slideToggle(500);
    });
    
        // when on mobile set the a click to pull window up
        $( '.menu__content__options a' ).click( function() {
            if( $( window ).width() < 769 ) {
                $( '.menu__content__options' ).slideUp(500);
            };
        });
    // on window resize set function for menu
    $( window ).resize( function() {

        if ( $( window ).width() > 769 ) {
            $( '.menu__content__options' ).show().css( 'display' , '' ); // jquery slideToggle sets display block, this resets to null
        }
        else if ( $( window ).width() < 769 ) {
            $( '.menu__content__options' ).slideUp(); // if the window is less than 769px all the menu options to slide up
        }

    });







    // function for social share links with pop up menu
    $('.share__btn').click(function(event){
        event.preventDefault();
        var service = $(this).data('service');
        switch(service) {
            case 'facebook':
                url = ( LeadPageData['facebookurl']['value'] ? LeadPageData['facebookurl']['value'] : document.URL);
                window_size = "width=585,height=368";
                go = 'http://www.facebook.com/sharer/sharer.php?u=' + url;
                break;
            case 'twitter':
                url = ( LeadPageData['twitterurl']['value'] ? LeadPageData['twitterurl']['value'] : document.URL);
                window_size = "width=585,height=261";
                go = 'http://www.twitter.com/intent/tweet?url=' + url;
                break;
            default:
                return false;
        }
        window.open(go, '', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,' + window_size);
    });



});


