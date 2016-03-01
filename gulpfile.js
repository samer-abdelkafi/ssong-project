var gulp = require('gulp');
var concat = require('gulp-concat');
var concatVendor = require('gulp-concat-vendor');
var uglify = require('gulp-uglify');
var minify = require('gulp-minify-css');
var mainBowerFiles = require('main-bower-files');
var inject = require('gulp-inject');
var runSequence = require('run-sequence');
var gzip = require('gulp-gzip');
var clone = require('gulp-clone');
var order = require('gulp-order');
var series = require('stream-series');
var flatten = require('gulp-flatten');
var rimraf = require('gulp-rimraf');
var debug = require('gulp-debug');
var uncss = require('gulp-uncss');
var spritesmith = require('gulp.spritesmith');

var vendorJs;
var vendorCss;
var appJs;
var appCss;

options = {
    html: ['src/main/webapp//**/*.html'],

    ignore: [
        '.show',
        '.hide',
        /\w\.in/,
        '.fade',
        '.collapse',
        '.collapsing',
        /(#|\.)has-error(\-[a-zA-Z]+)?/,
        /(#|\.)navbar(\-[a-zA-Z]+)?/,
        /(#|\.)dropdown(\-[a-zA-Z]+)?/,
        /(#|\.)is(\-[a-zA-Z]+)?/,
        /(#|\.)checkbox(\-[a-zA-Z]+)?/,
        /(#|\.)swagger(\-[a-zA-Z]+)?/,
        /(#|\.)pull(\-[a-zA-Z]+)?/,
        /(#|\.)list(\-[a-zA-Z]+)?/,
        /(#|\.)(open)/,
        /(#|\.)ripple(\-[a-zA-Z]+)?/,
        '.clearfix',
        '.three-dots-row-spinner',
        'rotateplane'],
    report : true
};

gulp.task('clean', function () {

    gulp.src('src/main/webapp/resources/dist', {read: false})
        .pipe(rimraf());

    gulp.src('src/main/webapp/resources/vendor', {read: false})
        .pipe(rimraf());
});

gulp.task('lib-js-files', function () {
    vendorJs = gulp.src(mainBowerFiles('**/*.js'), {base: 'bower_components'})
        .pipe(debug({title: 'lib-js-files :'}))
        .pipe(concatVendor('lib.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('src/main/webapp/resources/vendor/js'));

    return vendorJs.pipe(clone())
        .pipe(gzip())
        .pipe(gulp.dest('src/main/webapp/resources/vendor/js'));
});

gulp.task('lib-css-files', function () {
    vendorCss = gulp.src(mainBowerFiles('**/*.css'), {base: 'bower_components'})
        .pipe(uncss(options))
        .pipe(debug({title: 'lib-css-files :'}))
        .pipe(minify())
        .pipe(concat('lib.min.css'))
        .pipe(gulp.dest('src/main/webapp/resources/vendor/css'));

    return vendorCss.pipe(clone())
        .pipe(clone())
        .pipe(gzip())
        .pipe(gulp.dest('src/main/webapp/resources/vendor/css'));
});

gulp.task('app-js-files', function () {
    appJs = gulp.src('src/main/webapp/resources/src/js/*js')
       .pipe(concatVendor('app.min.js'))
       .pipe(gulp.dest('src/main/webapp/resources/dist/js'));

    return appJs.pipe(clone())
        .pipe(gzip())
        .pipe(gulp.dest('src/main/webapp/resources/dist/js'));
});


gulp.task('app-css-files', function () {
    appCss = gulp.src('src/main/webapp/resources/src/css/*css')
        .pipe(minify())
        .pipe(concat('app.min.css'))
        .pipe(gulp.dest('src/main/webapp/resources/dist/css'));

    return appCss.pipe(clone())
        .pipe(clone())
        .pipe(gzip())
        .pipe(gulp.dest('src/main/webapp/resources/dist/css'));
});

gulp.task('copyFonts', function () {
    return gulp.src('bower_components/**/fonts/*.{ttf,woff,woff2,eof,svg}')
        .pipe(flatten())
        .pipe(gulp.dest('src/main/webapp/resources/vendor/fonts'));
});

gulp.task('copyImg', function () {
    return gulp.src('src/main/webapp/resources/src/img/*.{png,jpeg}')
        .pipe(flatten())
        .pipe(gulp.dest('src/main/webapp/resources/dist/img'));
});

gulp.task('sprites', function () {
    var spriteData = gulp.src('src/main/webapp/resources/src/img/techno/*.png').pipe(spritesmith({
        imgName: 'sprite.png',
        cssName: 'sprite.css',
        imgPath: '../img/sprite.png'
    }));

    // Pipe CSS stream through CSS optimizer and onto disk
    spriteData.css
        .pipe(gulp.dest('src/main/webapp/resources/src/css/'));

    // Pipe image stream through image optimizer and onto disk
    return spriteData.img
        .pipe(gulp.dest('src/main/webapp/resources/src/img'));

});

gulp.task('index', function () {
    var vendorSources = gulp.src(['src/main/webapp/resources/vendor/**/*.{js,css}'], {read: false})
        .pipe(debug({title: 'index:'}));
    var appSources = gulp.src(['src/main/webapp/resources/dist/**/*.{js,css}'], {read: false})
        .pipe(debug({title: 'index:'}));
    return gulp.src("src/main/webapp/index.html")
        .pipe(inject(series(vendorSources, appSources), {relative: true}))
        .pipe(gulp.dest('src/main/webapp'));
});

// Default Task
gulp.task('default', function () {
    runSequence('clean', 'lib-js-files', 'app-js-files', 'sprites', 'app-css-files', 'lib-css-files',
        'copyFonts', 'copyImg', "index");
});