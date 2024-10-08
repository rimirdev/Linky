package com.rimir.linky.data.source

import com.rimir.linky.data.Link

class LinkRepository(private val dataSource: LinkDataSource) {

    suspend fun insertLink(link: Link): Result<Long> {
        return dataSource.insertLink(link)
    }

    suspend fun insertLinks(links: List<Link>): Result<Unit> {
        return dataSource.insertLinks(links)
    }

    suspend fun getLinkList(): Result<List<Link>> {
        return dataSource.getLinkList()
    }

    suspend fun getPinnedLinkList(): Result<List<Link>> {
        return dataSource.getPinnedLinkList()
    }

    suspend fun getSortedLinkList(): Result<List<Link>> {
        return dataSource.getSortedLinkList()
    }

    suspend fun getSortedFolderLinkList(id: Int): Result<List<Link>> {
        return dataSource.getSortedFolderLinkList(id)
    }

    suspend fun getSortedLinkListByKeyword(keyword: String): Result<List<Link>> {
        return dataSource.getSortedLinkListByKeyword(keyword)
    }

    suspend fun getSortedFolderLinkListByKeyword(id: Int, keyword: String) : Result<List<Link>> {
        return dataSource.getSortedFolderLinkListByKeyword(id, keyword)
    }

    suspend fun updateLink(link: Link): Result<Int> {
        return dataSource.updateLink(link)
    }

    suspend fun updateClickCountByLinkId(linkId: Int, count: Int): Result<Int> {
        return dataSource.updateClickCountByLinkId(linkId, count)
    }

    suspend fun deleteLink(link: Link): Result<Int> {
        return dataSource.deleteLink(link)
    }

    suspend fun deleteLinkByID(id: Int): Result<Int> {
        return dataSource.deleteLinkByID(id)
    }

    suspend fun deleteAll(): Result<Int> {
        return dataSource.deleteAll()
    }
}