package com.edd.memestream.storage

import org.mapdb.BTreeMap

class SimpleMemeRepository(map: BTreeMap<String, SimpleMeme>) : Repository<String, SimpleMeme>(map)