/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author poison
 */
public interface TagLoader {

	Set<String> load(Long gameId, EnumSet<TagType> types);
}
