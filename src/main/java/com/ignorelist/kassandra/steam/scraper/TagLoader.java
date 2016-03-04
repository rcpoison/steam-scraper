/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import java.util.EnumSet;

/**
 *
 * @author poison
 */
public interface TagLoader {

	GameInfo load(Long gameId, EnumSet<TagType> types);

}
