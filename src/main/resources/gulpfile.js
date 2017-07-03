// Require packages
var gulp = require('gulp'),
	sass = require('gulp-sass'),
	concat = require('gulp-concat'),
	uglify = require('gulp-uglify'),
	cleanCSS = require('gulp-clean-css'),
	plumber = require('gulp-plumber'),
	rename = require('gulp-rename'),
	autoprefixer = require('gulp-autoprefixer'),
	babel = require('gulp-babel'),
	bs = require('browser-sync').create();

var bowerPath = './bower_components/';
var foundationJSPlugins = bowerPath + 'foundation-sites/dist/js/plugins/';
var targetPath = '../../../target/classes/';

var src = {
	scripts: './scripts/**/*.js',
	styles: './styles/**/*.scss',
	templates: './templates/**/*.html',
	images: 'public/uploads/**/*'
};
var dest = {
	js: 'public/js',
	css: 'public/css',
	html: 'templates/',
	img: 'public/uploads'
};

var scriptDependencies = [
	foundationJSPlugins + 'foundation.core.min.js',
	foundationJSPlugins + 'foundation.util.box.min.js',
	foundationJSPlugins + 'foundation.util.keyboard.min.js',
	foundationJSPlugins + 'foundation.util.mediaQuery.min.js',
	foundationJSPlugins + 'foundation.util.motion.min.js',
	foundationJSPlugins + 'foundation.util.triggers.min.js',
	foundationJSPlugins + 'foundation.util.touch.min.js',
	foundationJSPlugins + 'foundation.reveal.min.js',
	foundationJSPlugins + 'foundation.slider.min.js',
	src.scripts
];

// Scripts task
gulp.task('scripts', function(){
	return gulp.src(scriptDependencies)
	.pipe(plumber())
	.pipe(babel())
	.pipe(concat('app.js'))
	.pipe(rename({ suffix:'.min'}))
	.pipe(uglify())
	.pipe(gulp.dest(dest.js))
	.pipe(gulp.dest(targetPath + dest.js))
	.pipe(bs.stream({once:true}));
});

// SASS task
gulp.task('sass', function(){
	return gulp.src(src.styles)
	.pipe(plumber())
	.pipe(sass({
		includePaths: [
			bowerPath + 'foundation-sites/scss',
			bowerPath + 'font-awesome/scss'
		]
	}))
	.pipe(autoprefixer({
		browsers: ['last 2 versions', 'ie >= 9', 'and_chr >= 2.3']
	}))
	.pipe(rename({ suffix:'.min'}))
	.pipe(cleanCSS())
	.pipe(gulp.dest(dest.css))
	.pipe(gulp.dest(targetPath + dest.css))
	.pipe(bs.stream());
});

// Templates task
gulp.task('templates', function(){
	return gulp.src(src.templates)
	.pipe(plumber())
	.pipe(gulp.dest(dest.html))
	.pipe(gulp.dest(targetPath + dest.html))
	.pipe(bs.stream({once:true}));
});

// Images task
gulp.task('images', function(){
	return gulp.src(src.images)
	.pipe(gulp.dest(targetPath + dest.img));
});

// Serve content task
gulp.task('serve', function(){
	bs.init({
		proxy: 'YOUR_PROXY_URL'
	});

	gulp.watch(src.scripts, ['scripts']);
	gulp.watch(src.styles, ['sass']);
	gulp.watch(src.templates, ['templates']);
	gulp.watch(src.images, { cwd: './' }, ['images']);
});

// Default task
gulp.task('default', ['serve']);