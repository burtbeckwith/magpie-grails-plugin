class MagpiePluginUrlMappings{

	static mappings = {


        "/restfulMagpie/errands" {
            controller  = 'restful'
            action      = 'showAllErrands'
        }

        "/restfulMagpie/errands/$id" {
            controller  = 'restful'
            action      = 'showErrand'
        }

        "/restfulMagpie/errands/$id/fetches" {
            controller  = 'restful'
            action      = 'showFetchesForErrand'
        }

        "/restfulMagpie/fetches" {
            controller  = 'restful'
            action      = 'showAllFetches'
        }

        "/restfulMagpie/fetches/$id/contents" {
            controller  = 'restful'
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
