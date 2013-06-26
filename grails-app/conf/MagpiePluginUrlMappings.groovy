class MagpiePluginUrlMappings{

	static mappings = {

        "/restfulMagpie" {
            controller  = 'magpieRestful'
            action      = 'index'
        }

        "/restfulMagpie/errands" {
            controller  = 'magpieRestful'
            action      = [ GET: 'showAllErrands',
                            POST:'createErrand']
        }

        "/restfulMagpie/errands/$id" {
            controller  = 'magpieRestful'
            action      = [GET : 'showErrand']
        }

        "/restfulMagpie/errands/$id/fetches" {
            controller  = 'magpieRestful'
            action      = [
                            GET: 'showFetchesForErrand',
                            POST:'fetchErrand']
        }

        "/restfulMagpie/fetches" {
            controller  = 'magpieRestful'
            action      = 'showAllFetches'
        }

        "/restfulMagpie/fetches/$id/contents" {
            controller  = 'magpieRestful'
            action      = 'showContentsForFetch'
        }

        "/restfulMagpie/fetches/$id" {
            controller  = 'magpieRestful'
            action      = 'showFetch'
        }

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index") {
            controller  = 'magpieConsole'
            action      = 'index'
        }
		"500"(view:'/error')
	}
}
