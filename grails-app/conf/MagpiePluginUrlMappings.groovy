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
            action      = 'showErrand'
        }

        "/restfulMagpie/errands/$id/fetches" {
            controller  = 'magpieRestful'
            action      = 'showFetchesForErrand'
        }

        "/restfulMagpie/fetches" {
            controller  = 'magpieRestful'
            action      = 'showAllFetches'
        }

        "/restfulMagpie/fetches/$id/contents" {
            controller  = 'magpieRestful'
            action      = 'showContentsForFetch'
        }

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
