# Maintainer: poison <rc.poison@gmail.com>
_pkgname="steam-scraper"
pkgname="${_pkgname}-git"
pkgver=r53.70df7e2
pkgrel=1
pkgdesc="Scrapes game data from the Steam shop API/store pages and adds them as categories in the Steam client."
arch=(any)
url="https://github.com/rcpoison/steam-scraper"
license=('WTFPL')
depends=('java-runtime')
makedepends=('git' 'java-environment' 'maven') # 'bzr', 'git', 'mercurial' or 'subversion'
source=("${_pkgname}::git+https://github.com/rcpoison/steam-scraper.git")
md5sums=('SKIP')


pkgver() {
	cd "${srcdir}/${_pkgname}"
	printf "r%s.%s" "$(git rev-list --count HEAD)" "$(git rev-parse --short HEAD)"
}

build() {
	cd "${srcdir}/${_pkgname}"
	mvn clean install
	cp "${_pkgname}.sh" "${_pkgname}"
	cat target/steam-scraper-*.one-jar.jar >> "${_pkgname}"
}

package() {
	cd "${srcdir}/${_pkgname}"
	install -vD -m755 "${_pkgname}" "$pkgdir/usr/bin/${_pkgname}"
	#mkdir -pv "$pkgdir/usr/bin"
	#cp -v "${_pkgname}" "$pkgdir/usr/bin/"
	#chmod +x "$pkgdir/usr/bin/${_pkgname}"
}
