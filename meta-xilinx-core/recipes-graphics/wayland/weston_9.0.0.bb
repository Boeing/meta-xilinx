SUMMARY = "Weston, a Wayland compositor"
DESCRIPTION = "Weston is the reference implementation of a Wayland compositor"
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d79ee9e66bb0f95d3386a7acae780b70 \
                    file://libweston/compositor.c;endline=27;md5=6c53bbbd99273f4f7c4affa855c33c0a"

# We want this version to be "newer" then 10, only if libmali and mali400 are both enabled
ORIG_PE := "${PE}"
MALI_PE = "${@bb.utils.contains('DISTRO_FEATURES', 'libmali', '1', '${ORIG_PE}', d)}"
PE = "${@bb.utils.contains('MACHINE_FEATURES', 'mali400', '${MALI_PE}', '${ORIG_PE}', d)}"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://weston.png \
           file://weston.desktop \
           file://xwayland.weston-start \
           file://systemd-notify.weston-start \
           file://0001-weston-launch-Provide-a-default-version-that-doesn-t.patch \
           file://0001-tests-include-fcntl.h-for-open-O_RDWR-O_CLOEXEC-and-.patch \
           file://0001-meson.build-fix-incorrect-header.patch \
           file://0001-libweston-backend-drm-Re-order-gbm-destruction-at-DR.patch \
"

SRC_URI:append:libc-musl = " file://dont-use-plane-add-prop.patch "

SRC_URI[sha256sum] = "5cf5d6ce192e0eb15c1fc861a436bf21b5bb3b91dbdabbdebe83e1f83aa098fe"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit meson pkgconfig useradd features_check
# depends on virtual/egl
# weston-init requires pam enabled if started via systemd
REQUIRED_DISTRO_FEATURES = "opengl ${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"

DEPENDS = "libxkbcommon gdk-pixbuf pixman cairo glib-2.0"
DEPENDS += "wayland wayland-protocols libinput virtual/egl pango wayland-native"

LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'lto', '-Wl,-z,undefs', '', d)}"

WESTON_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:1])}"

EXTRA_OEMESON += "-Dbackend-default=auto -Dpipewire=false"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'kms fbdev wayland egl clients', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'xwayland', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'pam systemd x11', d)} \
                   ${@bb.utils.contains_any('DISTRO_FEATURES', 'wayland x11', '', 'headless', d)} \
                   launch \
                   image-jpeg \
                   screenshare \
                   shell-desktop \
                   shell-fullscreen \
                   shell-ivi"

#
# Compositor choices
#
# Weston on KMS
PACKAGECONFIG[kms] = "-Dbackend-drm=true,-Dbackend-drm=false,drm udev virtual/egl virtual/libgles2 virtual/libgbm mtdev"
# Weston on Wayland (nested Weston)
PACKAGECONFIG[wayland] = "-Dbackend-wayland=true,-Dbackend-wayland=false,virtual/egl virtual/libgles2"
# Weston on X11
PACKAGECONFIG[x11] = "-Dbackend-x11=true,-Dbackend-x11=false,virtual/libx11 libxcb libxcb libxcursor cairo"
# Headless Weston
PACKAGECONFIG[headless] = "-Dbackend-headless=true,-Dbackend-headless=false"
# Weston on framebuffer
PACKAGECONFIG[fbdev] = "-Dbackend-fbdev=true,-Dbackend-fbdev=false,udev mtdev"
# Weston on RDP
PACKAGECONFIG[rdp] = "-Dbackend-rdp=true,-Dbackend-rdp=false,freerdp"
# weston-launch
PACKAGECONFIG[launch] = "-Dweston-launch=true,-Dweston-launch=false,drm"
# VA-API desktop recorder
PACKAGECONFIG[vaapi] = "-Dbackend-drm-screencast-vaapi=true,-Dbackend-drm-screencast-vaapi=false,libva"
# Weston with EGL support
PACKAGECONFIG[egl] = "-Drenderer-gl=true,-Drenderer-gl=false,virtual/egl"
# Weston with lcms support
PACKAGECONFIG[lcms] = "-Dcolor-management-lcms=true,-Dcolor-management-lcms=false,lcms"
# Weston with webp support
PACKAGECONFIG[webp] = "-Dimage-webp=true,-Dimage-webp=false,libwebp"
# Weston with systemd-login support
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dlauncher-logind=true,-Dsystemd=false -Dlauncher-logind=false,systemd dbus"
# Weston with Xwayland support (requires X11 and Wayland)
PACKAGECONFIG[xwayland] = "-Dxwayland=true,-Dxwayland=false"
# colord CMS support
PACKAGECONFIG[colord] = "-Dcolor-management-colord=true,-Dcolor-management-colord=false,colord"
# Clients support
PACKAGECONFIG[clients] = "-Dsimple-clients=all -Ddemo-clients=true,-Dsimple-clients= -Ddemo-clients=false"
# Virtual remote output with GStreamer on DRM backend
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
# Weston with PAM support
PACKAGECONFIG[pam] = "-Dpam=true,-Dpam=false,libpam"
# Weston with screen-share support
PACKAGECONFIG[screenshare] = "-Dscreenshare=true,-Dscreenshare=false"
# Traditional desktop shell
PACKAGECONFIG[shell-desktop] = "-Dshell-desktop=true,-Dshell-desktop=false"
# Fullscreen shell
PACKAGECONFIG[shell-fullscreen] = "-Dshell-fullscreen=true,-Dshell-fullscreen=false"
# In-Vehicle Infotainment (IVI) shell
PACKAGECONFIG[shell-ivi] = "-Dshell-ivi=true,-Dshell-ivi=false"
# JPEG image loading support
PACKAGECONFIG[image-jpeg] = "-Dimage-jpeg=true,-Dimage-jpeg=false, jpeg"

do_install:append() {
	# Weston doesn't need the .la files to load modules, so wipe them
	rm -f ${D}/${libdir}/libweston-${WESTON_MAJOR_VERSION}/*.la

	# If X11, ship a desktop file to launch it
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}" ]; then
		install -d ${D}${datadir}/applications
		install ${UNPACKDIR}/weston.desktop ${D}${datadir}/applications

		install -d ${D}${datadir}/icons/hicolor/48x48/apps
		install ${UNPACKDIR}/weston.png ${D}${datadir}/icons/hicolor/48x48/apps
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', 'yes', 'no', d)}" = "yes" ]; then
		install -Dm 644 ${UNPACKDIR}/xwayland.weston-start ${D}${datadir}/weston-start/xwayland
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'yes', 'no', d)}" = "yes" ]; then
		install -Dm 644 ${UNPACKDIR}/systemd-notify.weston-start ${D}${datadir}/weston-start/systemd-notify
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'launch', 'yes', 'no', d)}" = "yes" ]; then
		chmod u+s ${D}${bindir}/weston-launch
	fi
}

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', '${PN}-xwayland', '', d)} \
             libweston-${WESTON_MAJOR_VERSION} ${PN}-examples"

FILES:${PN}-dev += "${libdir}/${BPN}/libexec_weston.so"
FILES:${PN} = "${bindir}/weston ${bindir}/weston-terminal ${bindir}/weston-info ${bindir}/weston-launch ${bindir}/wcap-decode ${libexecdir} ${libdir}/${BPN}/*.so* ${datadir}"

FILES:libweston-${WESTON_MAJOR_VERSION} = "${libdir}/lib*${SOLIBS} ${libdir}/libweston-${WESTON_MAJOR_VERSION}/*.so"
SUMMARY:libweston-${WESTON_MAJOR_VERSION} = "Helper library for implementing 'wayland window managers'."

FILES:${PN}-examples = "${bindir}/*"

FILES:${PN}-xwayland = "${libdir}/libweston-${WESTON_MAJOR_VERSION}/xwayland.so"
RDEPENDS:${PN}-xwayland += "xwayland"

RDEPENDS:${PN} += "xkeyboard-config"
RRECOMMENDS:${PN} = "weston-init liberation-fonts"
RRECOMMENDS:${PN}-dev += "wayland-protocols"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system weston-launch"
