package org.system.types

/**
 * Created by evgeniikorniichuk on 06/06/15.
 */

trait Type

trait TransportType extends Type

trait ActionType extends Type

sealed trait Id

trait StepId extends Id

trait FileId extends Id

trait TransportId extends Id

trait TransportConfigId extends Id
